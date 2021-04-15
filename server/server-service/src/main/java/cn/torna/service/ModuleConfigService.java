package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.context.ModuleConfigKeys;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.DataIdUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.mapper.ModuleConfigMapper;
import cn.torna.service.dto.DocParamDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ModuleConfigService extends BaseService<ModuleConfig, ModuleConfigMapper> {

    @Autowired
    private DocParamService docParamService;


    public List<DocParam> listGlobalHeaders(long moduleId) {
        return this.listGlobal(moduleId, ModuleConfigTypeEnum.GLOBAL_HEADERS);
    }

    public List<DocParam> listGlobalParams(long moduleId) {
        return this.listGlobal(moduleId, ModuleConfigTypeEnum.GLOBAL_PARAMS);
    }

    public List<DocParam> listGlobalReturns(long moduleId) {
        return this.listGlobal(moduleId, ModuleConfigTypeEnum.GLOBAL_RETURNS);
    }

    public List<DocParam> listGlobal(long moduleId, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        List<Long> docIdList = this.listByModuleIdAndType(moduleId, moduleConfigTypeEnum)
                .stream()
                .map(ModuleConfig::getExtendId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(docIdList)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", docIdList);
        return docParamService.list(query);
    }


    public void addGlobal(DocParamDTO docParamDTO, long moduleId, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        this.saveDocParam(docParamDTO, moduleConfigTypeEnum, docParam -> {
            ModuleConfig moduleConfig = new ModuleConfig();
            moduleConfig.setModuleId(moduleId);
            moduleConfig.setType(moduleConfigTypeEnum.getType());
            moduleConfig.setExtendId(docParam.getId());
            save(moduleConfig);
        });
    }

    public void saveDocParam(DocParamDTO docParamDTO, ModuleConfigTypeEnum moduleConfigTypeEnum, Consumer<DocParam> callback) {
        ParamStyleEnum paramStyleEnum = buildStyle(moduleConfigTypeEnum);
        DocParam docParam = CopyUtil.copyBean(docParamDTO, DocParam::new);
        String dataId = DataIdUtil.getDocParamDataId(IdGen.genId(), 0L, paramStyleEnum.getStyle(), docParam.getName());
        docParam.setDataId(dataId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setRequired(Booleans.TRUE);
        docParamService.save(docParam);
        callback.accept(docParam);
    }

    public void updateGlobal(DocParamDTO docParamDTO) {
        DocParam docParam = docParamService.getById(docParamDTO.getId());
        CopyUtil.copyPropertiesIgnoreNull(docParamDTO, docParam);
        docParamService.update(docParam);
    }

    public void deleteGlobal(long moduleId, long extendId) {
        ModuleConfig moduleConfig = getByModuleIdAndExtendId(moduleId, extendId);
        this.delete(moduleConfig);
    }

    private ModuleConfig getByModuleIdAndExtendId(long moduleId, long extendId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("extend_id", extendId);
        return get(query);
    }

    public static ParamStyleEnum buildStyle(ModuleConfigTypeEnum moduleConfigTypeEnum) {
        ParamStyleEnum paramStyleEnum = ParamStyleEnum.REQUEST;
        switch (moduleConfigTypeEnum) {
            case GLOBAL_HEADERS:
                paramStyleEnum = ParamStyleEnum.HEADER;
                break;
            case GLOBAL_PARAMS:
                paramStyleEnum = ParamStyleEnum.REQUEST;
                break;
            case GLOBAL_RETURNS:
                paramStyleEnum = ParamStyleEnum.RESPONSE;
                break;
            default:
        }
        return paramStyleEnum;
    }


    /**
     * 设置模块调试环境
     *
     * @param moduleId 模块id
     * @param name     环境名称
     * @param url      调试路径
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
     *
     * @param moduleId 模块id
     * @param name     环境名称
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