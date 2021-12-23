package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.mapper.ModuleEnvironmentMapper;
import cn.torna.service.dto.ModuleEnvironmentDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author tanghc
 */
@Service
public class ModuleEnvironmentService extends BaseService<ModuleEnvironment, ModuleEnvironmentMapper> {

    public ModuleEnvironment getFirst(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .orderby("id", Sort.ASC);
        return this.get(query);
    }


    /**
     * 设置调试环境
     */
    public void setEnvironment(ModuleEnvironmentDTO moduleEnvironmentSettingDTO) {
        Long moduleId = moduleEnvironmentSettingDTO.getModuleId();
        String name = moduleEnvironmentSettingDTO.getName();
        String url = moduleEnvironmentSettingDTO.getUrl();
        Byte isPublic = moduleEnvironmentSettingDTO.getIsPublic();
        ModuleEnvironment moduleEnvironment = getByModuleIdAndName(moduleId, name);
        if (moduleEnvironment == null) {
            moduleEnvironment = new ModuleEnvironment();
            moduleEnvironment.setModuleId(moduleId);
            moduleEnvironment.setName(name);
            moduleEnvironment.setUrl(url);
            moduleEnvironment.setIsPublic(isPublic);
            save(moduleEnvironment);
        } else {
            moduleEnvironment.setUrl(url);
            moduleEnvironment.setIsPublic(isPublic);
            update(moduleEnvironment);
        }
    }

    public ModuleEnvironment getByModuleIdAndName(long moduleId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("name", name);
        return get(query);
    }

    /**
     * 设置调试环境
     * @param moduleId 模块id
     * @param name 名称
     * @param url url
     * @param isPublic 是否公开
     */
    public void setDebugEnv(long moduleId, String name, String url, boolean isPublic) {
        ModuleEnvironmentDTO moduleEnvironmentSettingDTO = new ModuleEnvironmentDTO();
        moduleEnvironmentSettingDTO.setModuleId(moduleId);
        moduleEnvironmentSettingDTO.setName(name);
        moduleEnvironmentSettingDTO.setUrl(url);
        moduleEnvironmentSettingDTO.setIsPublic(Booleans.toValue(isPublic));
        setEnvironment(moduleEnvironmentSettingDTO);
    }

    /**
     * 设置模块调试环境
     *
     * @param moduleId 模块id
     * @param name     环境名称
     * @param url      调试路径
     */
    public void setDebugEnv(long moduleId, String name, String url) {
        this.setDebugEnv(moduleId, name, url, false);
    }

    /**
     * 查询模块对应的环境
     * @param moduleId 模块id
     * @return 返回环境
     */
    public List<ModuleEnvironment> listModuleEnvironment(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .orderby("id", Sort.ASC);
        return list(query);
    }

    /**
     * 删除模块调试环境
     *
     * @param moduleId 模块id
     * @param name     环境名称
     */
    public void deleteDebugEnv(long moduleId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("name", name);
        ModuleEnvironment moduleEnvironment = this.get(query);
        if (moduleEnvironment != null) {
            this.delete(moduleEnvironment);
        }
    }
}