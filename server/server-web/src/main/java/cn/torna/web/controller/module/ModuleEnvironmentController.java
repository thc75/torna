package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.GenerateUtil;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.ProjectUser;
import cn.torna.service.ModuleEnvironmentService;
import cn.torna.service.ModuleService;
import cn.torna.service.ProjectService;
import cn.torna.service.dto.ModuleEnvironmentCopyDTO;
import cn.torna.service.dto.ProjectDTO;
import cn.torna.web.controller.doc.vo.TreeVO;
import cn.torna.web.controller.module.param.ModuleEnvironmentSettingAddParam;
import cn.torna.web.controller.module.param.ModuleEnvironmentSettingUpdateParam;
import cn.torna.web.controller.module.vo.ModuleEnvironmentVO;
import cn.torna.web.controller.module.vo.TreeEnvVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模块配置-环境
 * @author tanghc
 */
@RestController
@RequestMapping("module/environment")
public class ModuleEnvironmentController {

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ModuleService moduleService;


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

    @PostMapping("/copy")
    public Result copy(@RequestBody ModuleEnvironmentCopyDTO param) {
        moduleEnvironmentService.copyEnvironment(param);
        return Result.ok();
    }

    @GetMapping("userenvs")
    public Result<List<TreeEnvVO>> userEnv() {
        // 获取空间下的项目
        User user = UserContext.getUser();
        List<Project> projects = projectService.listUserProject(user);
        List<TreeEnvVO> list = new ArrayList<>();
        for (Project project : projects) {
            TreeEnvVO projectVO = new TreeEnvVO(IdGen.nextId(), project.getName(), "");
            list.add(projectVO);
            List<Module> modules = moduleService.listProjectModules(project.getId());
            for (Module module : modules) {
                TreeEnvVO moduleVO = new TreeEnvVO(IdGen.nextId(), module.getName(), projectVO.getId());
                list.add(moduleVO);
                List<ModuleEnvironment> moduleEnvironments = moduleEnvironmentService.listModuleEnvironment(module.getId());
                for (ModuleEnvironment environment : moduleEnvironments) {
                    TreeEnvVO envVO = new TreeEnvVO(IdUtil.encode(environment.getId()), module.getName(), moduleVO.getId());
                    list.add(envVO);
                }
            }
        }
        return Result.ok(list);
    }

}
