package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Space;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleService;
import cn.torna.service.ProjectService;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.web.controller.doc.vo.TreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/view")
public class ViewController {

    private static final byte TYPE_PROJECT = 0;
    private static final byte TYPE_MODULE = 1;
    private static final byte TYPE_FOLDER = 2;
    private static final byte TYPE_DOC = 3;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DocInfoService docInfoService;

    @GetMapping("data")
    public Result<List<TreeVO>> data(@HashId Long spaceId) {
        // 获取空间下的项目
        User user = UserContext.getUser();
        Space space = spaceService.getById(spaceId);
        List<ProjectDTO> projectDTOS = projectService.listSpaceUserProject(spaceId, user);
        List<TreeVO> list = new ArrayList<>();
        for (ProjectDTO projectDTO : projectDTOS) {
            TreeVO projectVO = new TreeVO(IdGen.nextId(), projectDTO.getName(), "", TYPE_PROJECT);
            list.add(projectVO);
            List<Module> modules = moduleService.listProjectModules(projectDTO.getId());
            for (Module module : modules) {
                TreeVO moduleVO = new TreeVO(IdGen.nextId(), module.getName(), projectVO.getId(), TYPE_MODULE);
                list.add(moduleVO);
                List<DocInfo> docInfos = docInfoService.listDocMenuView(module.getId());
                String base = moduleVO.getId();
                for (DocInfo docInfo : docInfos) {
                    boolean isFolder = Booleans.isTrue(docInfo.getIsFolder());
                    String id = isFolder ? buildId(base, docInfo.getId()) : IdUtil.encode(docInfo.getId());
                    String parentId = buildParentId(base, docInfo.getParentId());
                    byte type = isFolder ? TYPE_FOLDER : TYPE_DOC;
                    TreeVO docInfoVO = new TreeVO(id, docInfo.getName(), parentId, type);
                    docInfoVO.setHttpMethod(docInfo.getHttpMethod());
                    docInfoVO.setDocType(docInfo.getType());
                    docInfoVO.setDocId(docInfo.getId());
                    // 如果是文档
                    if (!isFolder) {
                        docInfoVO.setUrl(docInfo.getUrl());
                        List<String> originInfo = Arrays.asList(
                                space.getName(),
                                projectDTO.getName(),
                                module.getName()
                                );
                        docInfoVO.setOrigin(String.join("/", originInfo));
                    }
                    list.add(docInfoVO);
                }
            }
        }
        calcDocCount(list);
        return Result.ok(list);
    }

    /**
     * 计算文档数量
     * @param list 文档列表
     */
    private static void calcDocCount(List<TreeVO> list) {
        Map<String, TreeVO> idMap = list.stream().collect(Collectors.toMap(TreeVO::getId, Function.identity()));
        TreeUtil.initParent(list, "", idMap);
        for (TreeVO treeVO : list) {
            // 如果是文档类型，父节点数量+1
            if (Objects.equals(treeVO.getType(), TYPE_DOC)) {
                TreeVO parent = treeVO.getParent();
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
    public static List<TreeVO> convertTree(List<TreeVO> list, String parentId, Map<String, TreeVO> idMap) {
        if (parentId == null) {
            parentId = "";
        }
        if (list == null) {
            return Collections.emptyList();
        }
        List<TreeVO> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TreeVO item = list.get(i);
            // 如果有子节点
            if (Objects.equals(item.getParentId(), parentId)) {
                List<TreeVO> children = convertTree(list, item.getId(), idMap);
                item.setChildren(children);
                TreeVO parent = idMap.get(item.getParentId());
                item.setParent(parent);
                temp.add(item);
            }
        }
        return temp;
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @GetMapping("detail")
    public Result<DocInfoDTO> detail(@HashId Long id) {
        DocInfoDTO docInfoDTO = docInfoService.getDocDetailView(id);
        return Result.ok(docInfoDTO);
    }

    private String buildId(String base, Long id) {
        return DigestUtils.md5DigestAsHex((base + id).getBytes(StandardCharsets.UTF_8));
    }

    private String buildParentId(String base, Long parentId) {
        if (parentId == null || parentId == 0) {
            return base;
        }
        return buildId(base, parentId);
    }

}
