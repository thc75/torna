package torna.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.NoLogin;
import torna.common.bean.LoginUser;
import torna.common.bean.Result;
import torna.common.context.UserContext;
import torna.service.UserInfoService;
import torna.web.controller.system.param.LoginForm;
import torna.web.controller.system.vo.LoginResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("login")
    @NoLogin
    public Result<LoginResult> login(@RequestBody @Valid LoginForm param) {
        String username = param.getUsername();
        String password = userInfoService.getDbPassword(username, param.getPassword());
        LoginUser loginUser = userInfoService.login(username, password);
        LoginResult loginResult = new LoginResult();
        loginResult.setToken(loginUser.getToken());
        return Result.ok(loginResult);
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
        String token = UserContext.getToken(request);
        userInfoService.logout(token);
        return Result.ok();
    }

}
