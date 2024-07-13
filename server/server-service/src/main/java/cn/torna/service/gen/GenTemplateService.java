package cn.torna.service.gen;

import cn.torna.common.bean.TreeData;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.GenTemplate;
import cn.torna.dao.mapper.GenTemplateMapper;
import cn.torna.service.gen.dto.GenTemplateDTO;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author tanghc
 */
@Service
public class GenTemplateService extends BaseLambdaService<GenTemplate, GenTemplateMapper> {

    public List<String> listGroupName() {
        return this.query()
                .listUniqueValue(GenTemplate::getGroupName);
    }

    public List<TreeData> listGroupTree() {
        Map<String, List<GenTemplateDTO>> groupMap = this.query()
                .select(GenTemplate::getId, GenTemplate::getName, GenTemplate::getGroupName)
                .list(genTemplate -> {
                    GenTemplateDTO genTemplateDTO = CopyUtil.copyBean(genTemplate, GenTemplateDTO::new);
                    if (ObjectUtils.isEmpty(genTemplateDTO.getGroupName())) {
                        genTemplateDTO.setGroupName("默认分组");
                    }
                    return genTemplateDTO;
                })
                .stream()
                .collect(Collectors.groupingBy(GenTemplateDTO::getGroupName));

        List<TreeData> list = new ArrayList<>();

        for (Map.Entry<String, List<GenTemplateDTO>> entry : groupMap.entrySet()) {
            TreeData treeData = new TreeData();
            treeData.setLabel(entry.getKey());
            treeData.setChildren(entry.getValue().stream().map(genTemplateDTO -> {
                TreeData child = new TreeData();
                child.setId(genTemplateDTO.getId());
                child.setLabel(genTemplateDTO.getName());
                return child;
            }).collect(Collectors.toList()));
            list.add(treeData);
        }

        return list;
    }

}
