package cn.torna.service;

import cn.torna.dao.entity.ComposeDoc;
import cn.torna.dao.mapper.ComposeDocMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ComposeDocService extends BaseLambdaService<ComposeDoc, ComposeDocMapper> {

    public List<ComposeDoc> listByProjectId(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        Query query = this.query()
                .eq(ComposeDoc::getProjectId, projectId)
                .orderByAsc(ComposeDoc::getOrderIndex);
        return list(query);
    }


    public List<ComposeDoc> listByProjectIdAndParentId(long projectId, Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        List<ComposeDoc> composeDocList = this.listByProjectId(projectId);
        if (CollectionUtils.isEmpty(composeDocList)) {
            return composeDocList;
        }
        Long finalParentId = parentId;
        return composeDocList.stream()
                .filter(composeDoc -> Objects.equals(composeDoc.getParentId(), finalParentId))
                .collect(Collectors.toList());
    }

}
