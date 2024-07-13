package cn.torna.service;

import cn.torna.dao.entity.ConstantInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.mapper.ConstantInfoMapper;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.service.dto.ModuleConstantInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author tanghc
 */
@Service
public class ConstantInfoService extends BaseLambdaService<ConstantInfo, ConstantInfoMapper> {

    @Resource
    private ModuleMapper moduleMapper;

    public String getDocConstantInfo(long docId) {
        Query query = this.query().eq(ConstantInfo::getDocId, docId);
        return getValue(query, ConstantInfo::getContent);
    }

    public String getProjectConstantInfo(long projectId) {
        Query query = this.query().eq(ConstantInfo::getProjectId, projectId);
        return getValue(query, ConstantInfo::getContent);
    }

    public String getModuleConstantInfo(long moduleId) {
        Query query = this.query().eq(ConstantInfo::getModuleId, moduleId);
        String content = getValue(query, ConstantInfo::getContent);
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
        ConstantInfo errorCodeInfo = get(ConstantInfo::getProjectId, projectId);
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
        ConstantInfo errorCodeInfo = get(ConstantInfo::getModuleId, moduleId);
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
