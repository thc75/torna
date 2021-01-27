package cn.torna.web.controller.system;

import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.Result;
import cn.torna.common.context.UserContext;
import cn.torna.service.UserInfoService;
import cn.torna.web.controller.system.param.LoginForm;
import cn.torna.web.controller.system.vo.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.NoLogin;

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

}
