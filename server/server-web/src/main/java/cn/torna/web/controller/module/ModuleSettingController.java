package cn.torna.web.controller.module;

import cn.torna.common.bean.Result;
import cn.torna.common.context.ModuleConfigKeys;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.ModuleService;
import cn.torna.web.controller.module.param.DebugEnvParam;
import cn.torna.web.controller.module.param.DebugEnvSaveParam;
import cn.torna.web.controller.module.param.ModuleAllowMethodSetParam;
import cn.torna.web.controller.module.param.ModuleConfigParam;
import cn.torna.web.controller.module.param.ModuleGlobalHeaderUpdateParam;
import cn.torna.web.controller.module.vo.ModuleConfigVO;
import cn.torna.web.controller.module.vo.ModuleSettingVO;
import cn.torna.web.controller.module.vo.ModuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.HashId;

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

    /**
     * 获取模块配置
     * @param moduleId 模块id
     * @return
     */
    @GetMapping("get")
    public Result<ModuleSettingVO> info(@HashId Long moduleId) {
        Module module = moduleService.getById(moduleId);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        List<ModuleConfig> globalHeaders = moduleConfigService.listGlobalHeaders(moduleId);
        String allowMethod = moduleConfigService.getAllowMethod(moduleId);
        List<ModuleConfig> debugEnvs = moduleConfigService.listDebugHost(moduleId);
        ModuleSettingVO moduleSettingVO = new ModuleSettingVO();
        moduleSettingVO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, ModuleConfigVO::new));
        moduleSettingVO.setAllowMethod(allowMethod);
        moduleSettingVO.setDebugEnvs(CopyUtil.copyList(debugEnvs, ModuleConfigVO::new));
        moduleSettingVO.setModuleVO(moduleVO);
        return Result.ok(moduleSettingVO);
    }

    /**
     * 允许方法设置
     * @param param
     * @return
     */
    @PostMapping("/allowMethod/set")
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

    @PostMapping("/debugEnv/save")
    public Result saveDebugHost(@RequestBody DebugEnvSaveParam param) {
        for (DebugEnvParam debugHostParam : param.getDebugEnvs()) {
            this.setDebugEnv(debugHostParam);
        }
        return Result.ok();
    }

    @PostMapping("/debugEnv/delete")
    public Result deleteDebugHost(@RequestBody DebugEnvParam param) {
        moduleConfigService.deleteDebugEnv(param.getModuleId(), param.getConfigKey());
        return Result.ok();
    }

    @PostMapping("/globalHeader/add")
    public Result addHadder(@RequestBody ModuleConfigParam param) {
        ModuleConfig systemConfig = new ModuleConfig();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        systemConfig.setModuleId(param.getModuleId());
        systemConfig.setType(ModuleConfigTypeEnum.GLOBAL_HEADERS.getType());
        moduleConfigService.save(systemConfig);
        return Result.ok();
    }

    @GetMapping("/globalHeader/list")
    public Result<List<ModuleConfigVO>> listHeader(@HashId Long moduleId) {
        List<ModuleConfig> moduleConfigs = moduleConfigService.listGlobalHeaders(moduleId);
        List<ModuleConfigVO> moduleConfigVOS = CopyUtil.copyList(moduleConfigs, ModuleConfigVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalHeader/update")
    public Result updateHeader(@RequestBody ModuleGlobalHeaderUpdateParam param) {
        ModuleConfig moduleConfig = moduleConfigService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleConfig);
        moduleConfigService.update(moduleConfig);
        return Result.ok();
    }

    @PostMapping("/globalHeader/delete")
    public Result deleteHeader(@RequestBody ModuleConfigParam systemConfig) {
        ModuleConfig moduleConfig = moduleConfigService.getById(systemConfig.getId());
        moduleConfigService.delete(moduleConfig);
        return Result.ok();
    }

}
