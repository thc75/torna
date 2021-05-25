package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ComposeDoc;
import cn.torna.dao.mapper.ComposeDocMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ComposeDocService extends BaseService<ComposeDoc, ComposeDocMapper> {

    public List<ComposeDoc> listByProjectIdAndParentId(long projectId, Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        List<ComposeDoc> composeDocList = list("project_id", projectId);
        if (CollectionUtils.isEmpty(composeDocList)) {
            return composeDocList;
        }
        Long finalParentId = parentId;
        return composeDocList.stream()
                .filter(composeDoc -> Objects.equals(composeDoc.getParentId(), finalParentId))
                .collect(Collectors.toList());
    }

}