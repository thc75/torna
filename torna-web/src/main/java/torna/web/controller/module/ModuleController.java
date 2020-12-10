package torna.web.controller.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.ModuleTypeEnum;
import torna.common.util.CopyUtil;
import torna.dao.entity.Module;
import torna.service.DocImportService;
import torna.service.ModuleService;
import torna.service.dto.ImportSwaggerDTO;
import torna.web.controller.module.param.ImportSwaggerParam;
import torna.web.controller.module.param.ModuleAddParam;
import torna.web.controller.module.param.ModuleDeleteParam;
import torna.web.controller.module.param.ModuleUpdateNameParam;
import torna.web.controller.module.vo.ModuleVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DocImportService docImportService;

    @GetMapping("info")
    public Result<ModuleVO> info(@HashId Long moduleId) {
        Module module = moduleService.getById(moduleId);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 获取项目模块
     * @param projectId
     * @return
     */
    @GetMapping("list")
    public Result<List<ModuleVO>> listModule(@HashId Long projectId) {
        List<Module> modules = moduleService.listProjectModules(projectId);
        List<ModuleVO> moduleVOS = CopyUtil.copyList(modules, ModuleVO::new);
        return Result.ok(moduleVOS);
    }

    /**
     * 添加模块
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result<ModuleVO> add(@RequestBody @Valid ModuleAddParam param) {
        User user = UserContext.getUser();
        Module module = moduleService.addModule(param.getName(), param.getProjectId(), user);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 修改模块名称
     * @param param
     * @return
     */
    @PostMapping("name/update")
    public Result updateName(@RequestBody @Valid ModuleUpdateNameParam param) {
        User user = UserContext.getUser();
        Module module = moduleService.getById(param.getId());
        module.setName(param.getName());
        module.setModifierId(user.getUserId());
        module.setModifyMode(user.getOperationModel());
        moduleService.update(module);
        return Result.ok();
    }

    /**
     * 删除模块
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid ModuleDeleteParam param) {
        User user = UserContext.getUser();
        moduleService.delete(param.getId(), user);
        return Result.ok();
    }

    /**
     * 导入swagger
     * @param param
     * @return
     */
    @PostMapping("import/swagger")
    public Result importSwaggerDoc(@RequestBody @Valid ImportSwaggerParam param) {
        ImportSwaggerDTO importSwaggerDTO = CopyUtil.copyBean(param, ImportSwaggerDTO::new);
        User user = UserContext.getUser();
        importSwaggerDTO.setUser(user);
        docImportService.importSwagger(importSwaggerDTO);
        return Result.ok();
    }

    /**
     * 刷新swagger
     * @param moduleId
     * @return
     */
    @GetMapping("refresh/swagger")
    public Result refreshSwaggerDoc(@HashId Long moduleId) {
        Module module = moduleService.getById(moduleId);
        if (module != null && module.getType() == ModuleTypeEnum.SWAGGER_IMPORT.getType()) {
            ImportSwaggerDTO importSwaggerDTO = CopyUtil.copyBean(module, ImportSwaggerDTO::new);
            User user = UserContext.getUser();
            importSwaggerDTO.setUser(user);
            docImportService.importSwagger(importSwaggerDTO);
        }
        return Result.ok();
    }

}
