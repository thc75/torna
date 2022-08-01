package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.context.ModuleConfigKeys;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.ModuleConfig;
import cn.torna.dao.entity.ModuleSwaggerConfig;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.ModuleSwaggerConfigService;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.web.controller.module.param.DebugEnvParam;
import cn.torna.web.controller.module.param.ModuleAllowMethodSetParam;
import cn.torna.web.controller.module.param.ModuleGlobalParam;
import cn.torna.web.controller.module.param.ModuleSwaggerConfigParam;
import cn.torna.web.controller.module.vo.DebugEnvVO;
import cn.torna.web.controller.module.vo.ModuleGlobalVO;
import cn.torna.web.controller.module.vo.ModuleSwaggerConfigVO;
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
    private ModuleConfigService moduleConfigService;

    @Autowired
    private ModuleSwaggerConfigService moduleSwaggerConfigService;

    @Deprecated
    @GetMapping("/debugEnv/list")
    public Result<List<DebugEnvVO>> listDebugHost(@HashId Long moduleId) {
        List<ModuleConfig> debugEnvs = moduleConfigService.listDebugHost(moduleId);
        List<DebugEnvVO> moduleConfigVOS = CopyUtil.copyList(debugEnvs, DebugEnvVO::new);
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

    private static List<ModuleGlobalVO> convertModuleGlobalVO(List<DocParam> docParams, long moduleId) {
        List<ModuleGlobalVO> moduleConfigVOS = CopyUtil.copyList(docParams, ModuleGlobalVO::new, vo -> {
            vo.setModuleId(moduleId);
        });
        return moduleConfigVOS;
    }

    // globalHeaders

    @PostMapping("/globalHeaders/add")
    public Result addHeader(@RequestBody ModuleGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        moduleConfigService.addGlobal(docParamDTO, param.getModuleId(), ModuleConfigTypeEnum.GLOBAL_HEADERS);
        return Result.ok();
    }

    @GetMapping("/globalHeaders/list")
    public Result<List<ModuleGlobalVO>> listHeader(@HashId Long moduleId) {
        List<DocParam> docParams = moduleConfigService.listGlobal(moduleId, ParamStyleEnum.HEADER);
        List<ModuleGlobalVO> moduleConfigVOS = convertModuleGlobalVO(docParams, moduleId);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalHeaders/update")
    public Result updateHeader(@RequestBody ModuleGlobalParam param) {
        this.updateGlobal(param);
        return Result.ok();
    }

    @PostMapping("/globalHeaders/delete")
    public Result deleteHeader(@RequestBody ModuleGlobalParam param) {
        this.deleteGlobal(param);
        return Result.ok();
    }

    private void updateGlobal(ModuleGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        moduleConfigService.updateGlobal(docParamDTO);
    }

    public void deleteGlobal(ModuleGlobalParam param) {
        moduleConfigService.deleteGlobal(param.getModuleId(), param.getId());
    }

    // globalParams

    @PostMapping("/globalParams/add")
    public Result addParams(@RequestBody ModuleGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        moduleConfigService.addGlobal(docParamDTO, param.getModuleId(), ModuleConfigTypeEnum.GLOBAL_PARAMS);
        return Result.ok();
    }

    @GetMapping("/globalParams/list")
    public Result<List<ModuleGlobalVO>> listParams(@HashId Long moduleId) {
        List<DocParam> docParams = moduleConfigService.listGlobalParams(moduleId);
        List<ModuleGlobalVO> moduleConfigVOS = convertModuleGlobalVO(docParams, moduleId);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalParams/update")
    public Result updateParams(@RequestBody ModuleGlobalParam param) {
        this.updateGlobal(param);
        return Result.ok();
    }

    @PostMapping("/globalParams/delete")
    public Result deleteParams(@RequestBody ModuleGlobalParam param) {
        this.deleteGlobal(param);
        return Result.ok();
    }

    // globalReturns

    @PostMapping("/globalReturns/add")
    public Result addReturns(@RequestBody ModuleGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        moduleConfigService.addGlobal(docParamDTO, param.getModuleId(), ModuleConfigTypeEnum.GLOBAL_RETURNS);
        return Result.ok();
    }

    @GetMapping("/globalReturns/list")
    public Result<List<ModuleGlobalVO>> listReturns(@HashId Long moduleId) {
        List<DocParam> docParams = moduleConfigService.listGlobalReturns(moduleId);
        List<ModuleGlobalVO> moduleConfigVOS = convertModuleGlobalVO(docParams, moduleId);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalReturns/update")
    public Result updateReturns(@RequestBody ModuleGlobalParam param) {
        this.updateGlobal(param);
        return Result.ok();
    }

    @PostMapping("/globalReturns/delete")
    public Result deleteReturns(@RequestBody ModuleGlobalParam param) {
        this.deleteGlobal(param);
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

    @GetMapping("/swaggerSetting/config/get")
    public Result<ModuleSwaggerConfigVO> getSwaggerConfig(@HashId Long moduleId) {
        ModuleSwaggerConfig moduleSwaggerConfig = moduleSwaggerConfigService.getByModuleId(moduleId);
        if (moduleSwaggerConfig == null) {
            return Result.ok();
        }
        ModuleSwaggerConfigVO moduleSwaggerConfigVO = CopyUtil.copyBean(moduleSwaggerConfig, ModuleSwaggerConfigVO::new);
        return Result.ok(moduleSwaggerConfigVO);
    }

    @PostMapping("/swaggerSetting/config/update")
    public Result updateSwaggerConfig(@RequestBody ModuleSwaggerConfigParam param) {
        ModuleSwaggerConfig moduleSwaggerConfig = moduleSwaggerConfigService.getById(param.getId());
        if (moduleSwaggerConfig == null) {
            return Result.ok();
        }
        CopyUtil.copyPropertiesIgnoreNull(param, moduleSwaggerConfig);
        moduleSwaggerConfigService.updateIgnoreNull(moduleSwaggerConfig);
        return Result.ok();
    }
}
