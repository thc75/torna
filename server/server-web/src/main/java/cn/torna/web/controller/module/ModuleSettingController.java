package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.context.ModuleConfigKeys;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.ModuleService;
import cn.torna.web.controller.module.param.DebugEnvParam;
import cn.torna.web.controller.module.param.ModuleAllowMethodSetParam;
import cn.torna.web.controller.module.param.ModuleConfigParam;
import cn.torna.web.controller.module.param.ModuleGlobalHeaderUpdateParam;
import cn.torna.web.controller.module.param.ModuleGlobalParamsUpdateParam;
import cn.torna.web.controller.module.param.ModuleGlobalReturnsUpdateParam;
import cn.torna.web.controller.module.vo.ModuleConfigVO;
import cn.torna.web.controller.module.vo.SwaggerSettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("module/setting")
public class ModuleSettingController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @PostMapping("/debugEnv/set")
    public Result setDebugEnv(@RequestBody DebugEnvParam param) {
        moduleConfigService.setDebugEnv(
                param.getModuleId(),
                param.getConfigKey(),
                param.getConfigValue()
        );
        return Result.ok();
    }

    @GetMapping("/debugEnv/list")
    public Result<List<ModuleConfigVO>> listDebugHost(@HashId Long moduleId) {
        List<ModuleConfig> debugEnvs = moduleConfigService.listDebugHost(moduleId);
        List<ModuleConfigVO> moduleConfigVOS = CopyUtil.copyList(debugEnvs, ModuleConfigVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/debugEnv/add")
    public Result addDebugHost(@RequestBody DebugEnvParam param) {
        ModuleConfig systemConfig = new ModuleConfig();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        systemConfig.setModuleId(param.getModuleId());
        systemConfig.setType(ModuleConfigTypeEnum.DEBUG_HOST.getType());
        moduleConfigService.save(systemConfig);
        return Result.ok();
    }

    @PostMapping("/debugEnv/update")
    public Result updateDebugHost(@RequestBody DebugEnvParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleConfig);
        moduleConfigService.update(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/debugEnv/delete")
    public Result deleteDebugHost(@RequestBody DebugEnvParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        moduleConfigService.delete(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/globalHeaders/add")
    public Result addHadder(@RequestBody ModuleConfigParam param) {
        ModuleConfig systemConfig = new ModuleConfig();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        systemConfig.setModuleId(param.getModuleId());
        systemConfig.setType(ModuleConfigTypeEnum.GLOBAL_HEADERS.getType());
        moduleConfigService.save(systemConfig);
        return Result.ok();
    }

    @GetMapping("/globalHeaders/list")
    public Result<List<ModuleConfigVO>> listHeader(@HashId Long moduleId) {
        List<ModuleConfig> moduleConfigs = moduleConfigService.listGlobalHeaders(moduleId);
        List<ModuleConfigVO> moduleConfigVOS = CopyUtil.copyList(moduleConfigs, ModuleConfigVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalHeaders/update")
    public Result updateHeader(@RequestBody ModuleGlobalHeaderUpdateParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleConfig);
        moduleConfigService.update(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/globalHeaders/delete")
    public Result deleteHeader(@RequestBody ModuleConfigParam systemConfig) {
        ModuleConfig moduleConfig = moduleConfigService.getById(systemConfig.getId());
        moduleConfigService.delete(moduleConfig);
        return Result.ok();
    }

    //globalParams

    @PostMapping("/globalParams/add")
    public Result addParams(@RequestBody ModuleConfigParam param) {
        ModuleConfig systemConfig = new ModuleConfig();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        systemConfig.setModuleId(param.getModuleId());
        systemConfig.setType(ModuleConfigTypeEnum.GLOBAL_PARAMS.getType());
        moduleConfigService.save(systemConfig);
        return Result.ok();
    }

    @GetMapping("/globalParams/list")
    public Result<List<ModuleConfigVO>> listParams(@HashId Long moduleId) {
        List<ModuleConfig> moduleConfigs = moduleConfigService.listGlobalParams(moduleId);
        List<ModuleConfigVO> moduleConfigVOS = CopyUtil.copyList(moduleConfigs, ModuleConfigVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalParams/update")
    public Result updateParams(@RequestBody ModuleGlobalParamsUpdateParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleConfig);
        moduleConfigService.update(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/globalParams/delete")
    public Result deleteParams(@RequestBody ModuleConfigParam systemConfig) {
        ModuleConfig moduleConfig = moduleConfigService.getById(systemConfig.getId());
        moduleConfigService.delete(moduleConfig);
        return Result.ok();
    }

    // globalReturns

    @PostMapping("/globalReturns/add")
    public Result addReturns(@RequestBody ModuleConfigParam param) {
        ModuleConfig systemConfig = new ModuleConfig();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        systemConfig.setModuleId(param.getModuleId());
        systemConfig.setType(ModuleConfigTypeEnum.GLOBAL_RETURNS.getType());
        moduleConfigService.save(systemConfig);
        return Result.ok();
    }

    @GetMapping("/globalReturns/list")
    public Result<List<ModuleConfigVO>> listReturns(@HashId Long moduleId) {
        List<ModuleConfig> moduleConfigs = moduleConfigService.listGlobalReturns(moduleId);
        List<ModuleConfigVO> moduleConfigVOS = CopyUtil.copyList(moduleConfigs, ModuleConfigVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalReturns/update")
    public Result updateReturns(@RequestBody ModuleGlobalReturnsUpdateParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleConfig);
        moduleConfigService.update(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/globalReturns/delete")
    public Result deleteReturns(@RequestBody ModuleConfigParam systemConfig) {
        ModuleConfig moduleConfig = moduleConfigService.getById(systemConfig.getId());
        moduleConfigService.delete(moduleConfig);
        return Result.ok();
    }


    @GetMapping("/swaggerSetting/get")
    public Result<SwaggerSettingVO> swaggerSetting(@HashId Long moduleId) {
        String allowMethod = moduleConfigService.getAllowMethod(moduleId);
        SwaggerSettingVO swaggerSettingVO = new SwaggerSettingVO();
        swaggerSettingVO.setAllowMethod(allowMethod);
        return Result.ok(swaggerSettingVO);
    }

    /**
     * 允许方法设置
     * @param param
     * @return
     */
    @PostMapping("/swaggerSetting/allowMethod/set")
    public Result allowMethod(@RequestBody ModuleAllowMethodSetParam param) {
        Long moduleId = param.getModuleId();
        ModuleConfig commonConfig = moduleConfigService.getCommonConfig(moduleId, ModuleConfigKeys.KEY_ALLOW_METHODS);
        String method =  param.getMethod();
        if (commonConfig == null) {
            commonConfig = new ModuleConfig();
            commonConfig.setModuleId(moduleId);
            commonConfig.setType(ModuleConfigTypeEnum.COMMON.getType());
            commonConfig.setConfigKey(ModuleConfigKeys.KEY_ALLOW_METHODS);
            commonConfig.setConfigValue(method);
            moduleConfigService.save(commonConfig);
        } else {
            commonConfig.setConfigValue(method);
            moduleConfigService.update(commonConfig);
        }
        return Result.ok();
    }
}
