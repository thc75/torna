package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ConstantInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.mapper.ConstantInfoMapper;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.service.dto.ModuleConstantInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;


/**
 * @author tanghc
 */
@Service
public class ConstantInfoService extends BaseService<ConstantInfo, ConstantInfoMapper> {

    @Resource
    private ModuleMapper moduleMapper;

    public String getDocConstantInfo(long docId) {
        Query query = new Query().eq("doc_id", docId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

    public String getProjectConstantInfo(long projectId) {
        Query query = new Query().eq("project_id", projectId);
        return this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
    }

    public String getModuleConstantInfo(long moduleId) {
        Query query = new Query().eq("module_id", moduleId);
        String content = this.getMapper().getBySpecifiedColumns(Collections.singletonList("content"), query, String.class);
        if (content == null) {
            content = "";
        }
        return content;
    }

    public ModuleConstantInfoDTO getModuleAndProjectConstant(long moduleId) {
        Module module = moduleMapper.getById(moduleId);
        String moduleConstantInfo = getModuleConstantInfo(moduleId);
        String projectConstantInfo = getProjectConstantInfo(module.getProjectId());
        ModuleConstantInfoDTO moduleConstantInfoDTO = new ModuleConstantInfoDTO();
        moduleConstantInfoDTO.setConstantModule(moduleConstantInfo);
        moduleConstantInfoDTO.setConstantProject(projectConstantInfo);
        return moduleConstantInfoDTO;
    }

    public void saveProjectConstantInfo(long projectId, String content) {
        ConstantInfo errorCodeInfo = get("project_id", projectId);
        if (errorCodeInfo == null) {
            errorCodeInfo = new ConstantInfo();
            errorCodeInfo.setProjectId(projectId);
            errorCodeInfo.setContent(content);
            this.save(errorCodeInfo);
        } else {
            errorCodeInfo.setContent(content);
            this.update(errorCodeInfo);
        }
    }

    public void saveModuleConstantInfo(long moduleId, String content) {
        ConstantInfo errorCodeInfo = get("module_id", moduleId);
        if (errorCodeInfo == null) {
            errorCodeInfo = new ConstantInfo();
            errorCodeInfo.setModuleId(moduleId);
            errorCodeInfo.setContent(content);
            this.save(errorCodeInfo);
        } else {
            errorCodeInfo.setContent(content);
            this.update(errorCodeInfo);
        }
    }


}