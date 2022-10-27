package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ErrorCodeInfo;
import cn.torna.dao.mapper.ErrorCodeInfoMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;


/**
 * @author tanghc
 */
@Service
public class ErrorCodeInfoService extends BaseService<ErrorCodeInfo, ErrorCodeInfoMapper> {

    public String getDocErrorCode(long docId) {
        Query query = new Query().eq("doc_id", docId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

    public String getProjectErrorCode(long projectId) {
        Query query = new Query().eq("project_id", projectId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

    public String getModuleErrorCode(long moduleId) {
        Query query = new Query().eq("module_id", moduleId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

    public void saveProjectErrorCode(long projectId, String content) {
        ErrorCodeInfo errorCodeInfo = get("project_id", projectId);
        if (errorCodeInfo == null) {
            errorCodeInfo = new ErrorCodeInfo();
            errorCodeInfo.setProjectId(projectId);
            errorCodeInfo.setContent(content);
            this.save(errorCodeInfo);
        } else {
            errorCodeInfo.setContent(content);
            this.update(errorCodeInfo);
        }
    }

    public void saveModuleErrorCode(long moduleId, String content) {
        ErrorCodeInfo errorCodeInfo = get("module_id", moduleId);
        if (errorCodeInfo == null) {
            errorCodeInfo = new ErrorCodeInfo();
            errorCodeInfo.setModuleId(moduleId);
            errorCodeInfo.setContent(content);
            this.save(errorCodeInfo);
        } else {
            errorCodeInfo.setContent(content);
            this.update(errorCodeInfo);
        }
    }


}