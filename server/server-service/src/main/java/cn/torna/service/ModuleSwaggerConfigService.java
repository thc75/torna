package cn.torna.service;

import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleSwaggerConfig;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import com.gitee.fastmybatis.core.support.IService;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class ModuleSwaggerConfigService implements IService<ModuleSwaggerConfig, Long> {

    public void create(ImportSwaggerV2DTO importSwaggerV2DTO, String content, Module module) {
        ModuleSwaggerConfig moduleSwaggerConfig = getByModuleId(module.getId());
        if (moduleSwaggerConfig == null) {
            moduleSwaggerConfig = new ModuleSwaggerConfig();
        }
        moduleSwaggerConfig.setModuleId(module.getId());
        moduleSwaggerConfig.setUrl(importSwaggerV2DTO.getUrl());
        moduleSwaggerConfig.setContent(content);
        moduleSwaggerConfig.setAuthUsername(importSwaggerV2DTO.getAuthUsername());
        moduleSwaggerConfig.setAuthPassword(importSwaggerV2DTO.getAuthPassword());
        if (moduleSwaggerConfig.getId() == null) {
            this.saveIgnoreNull(moduleSwaggerConfig);
        } else {
            this.updateIgnoreNull(moduleSwaggerConfig);
        }

    }

    public ModuleSwaggerConfig getByModuleId(Long moduleId) {
        return getByColumn("module_id", moduleId);
    }

}