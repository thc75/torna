package cn.torna.service;

import cn.torna.common.util.TreeUtil;
import cn.torna.service.dto.TreeDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
public class DocViewService {

    public static final byte TYPE_PROJECT = 0;
    public static final byte TYPE_MODULE = 1;
    public static final byte TYPE_FOLDER = 2;
    public static final byte TYPE_DOC = 3;


    /**
     * 计算文档数量
     * @param list 文档列表
     */
    public static void calcDocCount(List<TreeDTO> list) {
        Map<String, TreeDTO> idMap = list.stream().collect(Collectors.toMap(TreeDTO::getId, Function.identity()));
        TreeUtil.initParent(list, "", idMap);
        for (TreeDTO TreeDTO : list) {
            // 如果是文档类型，父节点数量+1
            if (Objects.equals(TreeDTO.getType(), TYPE_DOC)) {
                TreeDTO parent = TreeDTO.getParent();
                if (parent != null) {
                    parent.addApiCount();
                }
            }
        }
    }

    /**
     * list转换成tree
     * @param list
     * @param parentId 当前父节点id
     * @return
     */
    public static List<TreeDTO> convertTree(List<TreeDTO> list, String parentId, Map<String, TreeDTO> idMap) {
        if (parentId == null) {
            parentId = "";
        }
        if (list == null) {
            return Collections.emptyList();
        }
        List<TreeDTO> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TreeDTO item = list.get(i);
            // 如果有子节点
            if (Objects.equals(item.getParentId(), parentId)) {
                List<TreeDTO> children = convertTree(list, item.getId(), idMap);
                item.setChildren(children);
                TreeDTO parent = idMap.get(item.getParentId());
                item.setParent(parent);
                temp.add(item);
            }
        }
        return temp;
    }


    public static String buildId(String base, Long id) {
        return DigestUtils.md5DigestAsHex((base + id).getBytes(StandardCharsets.UTF_8));
    }

    public static String buildParentId(String base, Long parentId) {
        if (parentId == null || parentId == 0) {
            return base;
        }
        return buildId(base, parentId);
    }

}
