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
import torna.service.ModuleService;
import torna.web.controller.param.ProjectModuleAddParam;
import torna.web.controller.param.ProjectModuleDeleteParam;
import torna.web.controller.vo.ModuleVO;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("project/module")
public class ProjectModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("info")
    public Result<ModuleVO> info(long moduleId) {
        Module module = moduleService.getById(moduleId);
        ModuleVO moduleVO = CopyUtil.copyBean(module, ModuleVO::new);
        return Result.ok(moduleVO);
    }

    /**
     * 添加模块
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody ProjectModuleAddParam param) {
        User user = UserContext.getUser();
        Module module = CopyUtil.copyBean(param, Module::new);
        module.setType(ModuleTypeEnum.CUSTOM_ADD.getType());
        module.setCreatorId(user.getUserId());
        module.setModifierId(user.getUserId());
        moduleService.saveIgnoreNull(module);
        return Result.ok(module.getId());
    }

    /**
     * 删除模块
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody ProjectModuleDeleteParam param) {
        Module module = moduleService.getById(param.getId());
        moduleService.delete(module);
        return Result.ok();
    }

}
