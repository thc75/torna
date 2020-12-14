package torna.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.service.PermissionService;
import torna.service.dto.UserPermDTO;

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
