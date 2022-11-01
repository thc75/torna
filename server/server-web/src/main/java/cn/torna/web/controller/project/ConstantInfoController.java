package cn.torna.web.controller.project;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.service.ConstantInfoService;
import cn.torna.service.ModuleService;
import cn.torna.service.dto.ModuleConstantInfoDTO;
import cn.torna.web.controller.project.param.ConstantInfoSaveParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author thc
 */
@RestController
@RequestMapping("constant")
public class ConstantInfoController {

    @Autowired
    private ConstantInfoService constantInfoService;

    @Autowired
    private ModuleService moduleService;

    @GetMapping("project/get")
    public Result<String> get(@HashId Long projectId) {
        String content = constantInfoService.getProjectConstantInfo(projectId);
        return Result.ok(content);
    }

    @PostMapping("project/save")
    public Result<String> update(@Valid @RequestBody ConstantInfoSaveParam param) {
        constantInfoService.saveProjectConstantInfo(param.getId(), param.getContent());
        return Result.ok();
    }

    @GetMapping("module/get")
    public Result<String> getModule(@HashId Long moduleId) {
        String content = constantInfoService.getModuleConstantInfo(moduleId);
        return Result.ok(content);
    }

    @PostMapping("module/save")
    public Result<String> updateModule(@Valid @RequestBody ConstantInfoSaveParam param) {
        constantInfoService.saveModuleConstantInfo(param.getId(), param.getContent());
        return Result.ok();
    }

    @GetMapping("module/all")
    public Result<ModuleConstantInfoDTO> getModuleAll(@HashId Long moduleId) {
        ModuleConstantInfoDTO moduleAndProjectConstant = constantInfoService.getModuleAndProjectConstant(moduleId);
        return Result.ok(moduleAndProjectConstant);
    }
}
