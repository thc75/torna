package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.dao.entity.Module;
import cn.torna.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("module/token")
public class ModuleTokenController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("get")
    public Result<String> get(@HashId long moduleId) {
        Module module = moduleService.getById(moduleId);
        String token = module.getToken();
        return Result.ok(token);
    }

    @GetMapping("refresh")
    public Result<String> refresh(@HashId long moduleId) {
        Module module = moduleService.getById(moduleId);
        String token = ModuleService.createToken();
        module.setToken(token);
        moduleService.update(module);
        return Result.ok(token);
    }

}
