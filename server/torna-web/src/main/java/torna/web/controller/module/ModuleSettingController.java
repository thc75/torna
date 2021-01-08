package torna.web.controller.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.context.ModuleConfigKeys;
import torna.common.enums.ModuleConfigTypeEnum;
import torna.common.util.CopyUtil;
import torna.dao.entity.Module;
import torna.dao.entity.ModuleConfig;
import torna.service.ModuleConfigService;
import torna.service.ModuleService;
import torna.web.controller.module.param.DebugHostParam;
import torna.web.controller.module.param.ModuleAllowMethodSetParam;
import torna.web.controller.module.param.ModuleConfigParam;
import torna.web.controller.module.param.ModuleGlobalHeaderUpdateParam;
import torna.web.controller.module.vo.ModuleConfigVO;
import torna.web.controller.module.vo.ModuleSettingVO;
import torna.web.controller.module.vo.ModuleVO;

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
        String baseUrl = moduleConfigService.getBaseUrl(moduleId);
        ModuleSettingVO moduleSettingVO = new ModuleSettingVO();
        moduleSettingVO.setGlobalHeaders(CopyUtil.copyList(globalHeaders, ModuleConfigVO::new));
        moduleSettingVO.setAllowMethod(allowMethod);
        moduleSettingVO.setBaseUrl(baseUrl);
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

    /**
     * 设置调试host
     * @param param
     * @return
     */
    @PostMapping("/baseurl/set")
    public Result setBaseUrl(@RequestBody DebugHostParam param) {
        Long moduleId = param.getModuleId();
        String baseUrl = param.getBaseUrl();
        moduleConfigService.setBaseUrl(moduleId, baseUrl);
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
