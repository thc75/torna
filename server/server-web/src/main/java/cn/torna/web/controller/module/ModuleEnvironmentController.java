package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.service.ModuleEnvironmentService;
import cn.torna.web.controller.module.param.ModuleEnvironmentSettingAddParam;
import cn.torna.web.controller.module.param.ModuleEnvironmentSettingUpdateParam;
import cn.torna.web.controller.module.vo.ModuleEnvironmentVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 模块配置-环境
 * @author tanghc
 */
@RestController
@RequestMapping("module/environment")
public class ModuleEnvironmentController {

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;


    @GetMapping("/list")
    public Result<List<ModuleEnvironmentVO>> listEnv(@HashId Long moduleId) {
        List<ModuleEnvironment> moduleEnvironments = moduleEnvironmentService.listModuleEnvironment(moduleId);
        List<ModuleEnvironmentVO> list = CopyUtil.copyList(moduleEnvironments, ModuleEnvironmentVO::new);
        return Result.ok(list);
    }

    @PostMapping("/add")
    public Result addDebugHost(@RequestBody @Valid ModuleEnvironmentSettingAddParam param) {
        ModuleEnvironment environment = moduleEnvironmentService.getByModuleIdAndName(param.getModuleId(), param.getName());
        if (environment != null) {
            throw new BizException(param.getName() + " 已存在");
        }
        ModuleEnvironment systemConfig = new ModuleEnvironment();
        CopyUtil.copyPropertiesIgnoreNull(param, systemConfig);
        moduleEnvironmentService.save(systemConfig);
        return Result.ok();
    }

    @PostMapping("/update")
    public Result updateDebugHost(@RequestBody @Valid ModuleEnvironmentSettingUpdateParam param) {
        ModuleEnvironment environment = moduleEnvironmentService.getByModuleIdAndName(param.getModuleId(), param.getName());
        if (environment != null && !Objects.equals(environment.getId(), param.getId())) {
            throw new BizException(param.getName() + " 已存在");
        }
        ModuleEnvironment moduleEnvironment = moduleEnvironmentService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleEnvironment);
        moduleEnvironmentService.update(moduleEnvironment);
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result deleteDebugHost(@RequestBody IdParam param) {
        ModuleEnvironment moduleEnvironment = moduleEnvironmentService.getById(param.getId());
        moduleEnvironmentService.delete(moduleEnvironment);
        return Result.ok();
    }

}
