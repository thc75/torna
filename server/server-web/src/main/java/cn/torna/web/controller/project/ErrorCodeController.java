package cn.torna.web.controller.project;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.service.ErrorCodeInfoService;
import cn.torna.web.controller.project.param.ErrorCodeSaveParam;
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
@RequestMapping("code")
public class ErrorCodeController {

    @Autowired
    private ErrorCodeInfoService errorCodeInfoService;

    @GetMapping("project/get")
    public Result<String> get(@HashId Long projectId) {
        String content = errorCodeInfoService.getProjectErrorCode(projectId);
        return Result.ok(content);
    }

    @PostMapping("project/save")
    public Result<String> update(@Valid @RequestBody ErrorCodeSaveParam param) {
        errorCodeInfoService.saveProjectErrorCode(param.getId(), param.getContent());
        return Result.ok();
    }

    @GetMapping("module/get")
    public Result<String> getModule(@HashId Long moduleId) {
        String content = errorCodeInfoService.getModuleErrorCode(moduleId);
        return Result.ok(content);
    }

    @PostMapping("module/save")
    public Result<String> updateModule(@Valid @RequestBody ErrorCodeSaveParam param) {
        errorCodeInfoService.saveModuleErrorCode(param.getId(), param.getContent());
        return Result.ok();
    }
}
