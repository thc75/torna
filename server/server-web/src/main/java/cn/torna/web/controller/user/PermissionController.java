package cn.torna.web.controller.user;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.service.PermissionService;
import cn.torna.service.dto.UserPermDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("user")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取用户权限
     * @return
     */
    @GetMapping("perm/get")
    public Result<UserPermDTO> getUserPerm() {
        User user = UserContext.getUser();
        UserPermDTO userPerm = permissionService.getUserPerm(user);
        return Result.ok(userPerm);
    }

}
