package cn.torna.service;

import cn.torna.common.context.ModuleConfigKeys;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.mapper.ModuleConfigMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author tanghc
 */
@Service
public class ModuleConfigService extends BaseService<ModuleConfig, ModuleConfigMapper> {

    public List<ModuleConfig> listGlobalHeaders(long moduleId) {
        return this.listByModuleIdAndType(moduleId, ModuleConfigTypeEnum.GLOBAL_HEADERS);
    }

    /**
     * 设置模块调试环境
     * @param moduleId 模块id
     * @param name 环境名称
     * @param url 调试路径
     */
    public void setDebugEnv(long moduleId, String name, String url) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("type", ModuleConfigTypeEnum.DEBUG_HOST.getType())
                .eq("config_key", name);
        ModuleConfig commonConfig = this.get(query);
        if (commonConfig == null) {
            commonConfig = new ModuleConfig();
            commonConfig.setModuleId(moduleId);
            commonConfig.setType(ModuleConfigTypeEnum.DEBUG_HOST.getType());
            commonConfig.setConfigKey(name);
            commonConfig.setConfigValue(url);
            save(commonConfig);
        } else {
            commonConfig.setConfigValue(url);
            update(commonConfig);
        }
    }

    /**
     * 删除模块调试环境
     * @param moduleId 模块id
     * @param name 环境名称
     */
    public void deleteDebugEnv(long moduleId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("type", ModuleConfigTypeEnum.DEBUG_HOST.getType())
                .eq("config_key", name);
        ModuleConfig commonConfig = this.get(query);
        if (commonConfig != null) {
            this.delete(commonConfig);
        }
    }

    public List<ModuleConfig> listDebugHost(long moduleId) {
        return this.listByModuleIdAndType(moduleId, ModuleConfigTypeEnum.DEBUG_HOST);
    }

    public List<ModuleConfig> listByModuleIdAndType(long moduleId, ModuleConfigTypeEnum typeEnum) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("type", typeEnum.getType());
        return this.listAll(query);
    }

    public static String getDebugHostKey(long moduleId) {
        return "debughost_" + moduleId;
    }

    public String getAllowMethod(long moduleId) {
        return getCommonConfigValue(moduleId, ModuleConfigKeys.KEY_ALLOW_METHODS, "POST").toUpperCase();
    }

    public String getBaseUrl(long moduleId) {
        String debugHostKey = getDebugHostKey(moduleId);
        return getCommonConfigValue(moduleId, debugHostKey, "");
    }

    public ModuleConfig getCommonConfig(long moduleId, String key) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("type", ModuleConfigTypeEnum.COMMON.getType())
                .eq("config_key", key);
        return get(query);
    }

    public String getCommonConfigValue(long moduleId, String key, String defaultValue) {
        ModuleConfig commonConfig = getCommonConfig(moduleId, key);
        return Optional.ofNullable(commonConfig)
                .map(ModuleConfig::getConfigValue)
                .orElse(defaultValue);
    }

    public void setBaseUrl(long moduleId, String baseUrl) {
        String key = ModuleConfigService.getDebugHostKey(moduleId);
        ModuleConfig commonConfig = getCommonConfig(moduleId, key);
        if (commonConfig == null) {
            commonConfig = new ModuleConfig();
            commonConfig.setModuleId(moduleId);
            commonConfig.setType(ModuleConfigTypeEnum.COMMON.getType());
            commonConfig.setConfigKey(key);
            commonConfig.setConfigValue(baseUrl);
            save(commonConfig);
        } else {
            commonConfig.setConfigValue(baseUrl);
            update(commonConfig);
        }
    }

}