package cn.torna.service;

import cn.torna.dao.entity.ProjectReleaseDoc;
import cn.torna.dao.mapper.ProjectReleaseDocMapper;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiuyu
 */
@Service
public class ProjectReleaseDocService extends BaseLambdaService<ProjectReleaseDoc, ProjectReleaseDocMapper> {

    public List<ProjectReleaseDoc> getReleaseDocs(Long projectId, Long releaseId) {
        Query query = LambdaQuery.create(ProjectReleaseDoc.class)
                .eq(ProjectReleaseDoc::getProjectId, projectId)
                .eq(ProjectReleaseDoc::getReleaseId, releaseId);
        return list(query);
    }
}
