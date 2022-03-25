package cn.torna.web.controller.system;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.LoginDTO;
import cn.torna.web.controller.system.param.LoginForm;
import cn.torna.web.controller.system.param.UpdatePasswordByFirstLoginParam;
import cn.torna.web.controller.system.vo.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserCacheManager userCacheManager;

    @PostMapping("login")
    @NoLogin
    public Result<LoginResult> login(@RequestBody @Valid LoginForm param) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(param.getUsername());
        loginDTO.setPassword(param.getPassword());
        loginDTO.setUserInfoSourceEnum(UserInfoSourceEnum.of(param.getSource()));
        LoginUser loginUser = userInfoService.login(loginDTO);
        LoginResult loginResult = new LoginResult();
        loginResult.setToken(loginUser.getToken());
        loginResult.setStatus(loginUser.getStatus());
        return Result.ok(loginResult);
    }

    @PostMapping("/password/updateByFirstLogin")
    public Result updateByFirstLogin(@RequestBody @Valid UpdatePasswordByFirstLoginParam param) {
        User user = UserContext.getUser();
        long userId = user.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        if (UserStatusEnum.of(userInfo.getStatus()) == UserStatusEnum.SET_PASSWORD) {
            String newPwdHex = userInfoService.getDbPassword(userInfo.getUsername(), param.getPassword());
            userInfo.setPassword(newPwdHex);
            userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
            userInfoService.update(userInfo);
            // 重新设置缓存
            LoginUser loginUser = CopyUtil.copyBean(userInfo, LoginUser::new);
            userCacheManager.saveUser(loginUser);
        }
        return Result.ok();
    }

}
