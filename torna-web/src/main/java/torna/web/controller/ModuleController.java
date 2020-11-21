package torna.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.ModuleTypeEnum;
import torna.common.util.CopyUtil;
import torna.dao.entity.Module;
import torna.service.DocImportService;
import torna.service.ModuleService;
import torna.service.dto.ImportSwaggerDTO;
import torna.web.controller.param.ImportSwaggerParam;
import torna.web.controller.param.ModuleAddParam;
import torna.web.controller.param.ModuleDeleteParam;
import torna.web.controller.vo.ModuleVO;

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
    public Result<ModuleVO> info(long moduleId) {
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
    public Result<List<ModuleVO>> listModule(long projectId) {
        List<Module> modules = moduleService.list("project_id", projectId);
        List<ModuleVO> moduleVOS = CopyUtil.copyList(modules, ModuleVO::new);
        return Result.ok(moduleVOS);
    }

    /**
     * 添加模块
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result<ModuleVO> add(@RequestBody ModuleAddParam param) {
        User user = UserContext.getUser();
        Module module = CopyUtil.copyBean(param, Module::new);
        module.setType(ModuleTypeEnum.CUSTOM_ADD.getType());
        module.setCreatorId(user.getUserId());
        module.setModifierId(user.getUserId());
        moduleService.saveIgnoreNull(module);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 删除模块
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody ModuleDeleteParam param) {
        Module module = moduleService.getById(param.getId());
        moduleService.delete(module);
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
    public Result refreshSwaggerDoc(long moduleId) {
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
