package cn.torna.web.controller.system;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.LoginUser;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.util.JwtUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.LoginDTO;
import cn.torna.web.controller.system.param.ThirdLoginForm;
import cn.torna.web.controller.system.vo.LoginResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

/**
 * @author 六如
 */
@Controller
public class ThirdLoginController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("thirdLogin")
    @NoLogin
    public void thirdLogin(@Valid ThirdLoginForm param, HttpServletResponse response) {
        try {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(param.getUsername());
            String password = param.getPassword();
            password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
            loginDTO.setPassword(password);
            loginDTO.setUserInfoSourceEnum(UserInfoSourceEnum.of(param.getSource()));
            LoginUser loginUser = userInfoService.login(loginDTO);
            LoginResult loginResult = new LoginResult();
            loginResult.setToken(loginUser.getToken());
            loginResult.setStatus(loginUser.getStatus());
            loginResult.setMfaEnable(Boolean.parseBoolean(EnvironmentKeys.TORNA_MFA_ENABLE.getValue()));
            loginResult.setIsFirstMfaAuth(loginUser.getIsFirstMfaAuth());
            String redirectUrl = param.getRedirectUrl();
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = EnvironmentKeys.TORNA_FRONT_URL.getValue("http://localhost:7700");
            }
            // 做页面跳转，主要是保存token
            ResponseUtil.writeHtml(response, JwtUtil.getJumpPageHtml(loginUser.getToken(), redirectUrl));
        } catch (Exception e) {
            ResponseUtil.writeText(response, e.getMessage());
        }
    }

}
