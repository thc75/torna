package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import torna.common.enums.ModuleConfigTypeEnum;
import torna.common.support.BaseService;
import torna.dao.entity.ModuleConfig;
import torna.dao.mapper.ModuleConfigMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author tanghc
 */
@Service
public class ModuleConfigService extends BaseService<ModuleConfig, ModuleConfigMapper> {

    public static final String KEY_ALLOW_METHODS = "allow-methods";

    public List<ModuleConfig> listGlobalHeaders(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("type", ModuleConfigTypeEnum.GLOBAL_HEADERS.getType());
        return this.listAll(query);
    }

    public static String getDebugHostKey(long moduleId) {
        return "debughost_" + moduleId;
    }

    public String getAllowMethods(long moduleId) {
        return getCommonConfigValue(moduleId, KEY_ALLOW_METHODS, "GET").toUpperCase();
    }

    public String getDebugHost(long moduleId) {
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

}