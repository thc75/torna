package torna.web.controller.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.util.GenerateUtil;
import torna.dao.entity.Module;
import torna.service.ModuleService;

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
        moduleService.updateIgnoreNull(module);
        return Result.ok(token);
    }

}
