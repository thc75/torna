package cn.torna.web.controller.system;

import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.JwtUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.service.SystemLoginTokenService;
import cn.torna.service.UserInfoService;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 基于https://gitee.com/yadong.zhang/JustAuth
 */
@Slf4j
@Controller
@RequestMapping("system/oauth")
public class OauthController {

    @Autowired(required = false)
    private AuthRequestFactory factory;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SystemLoginTokenService systemLoginTokenService;

    @Value("${torna.front-host:}")
    private String frontHost;

    @Value("${torna.login.third-party.oauth.success-url:/}")
    private String successUrl;


    /**
     * 认证登录，跳转到登录页
     *
     * @param type     类型，如：custom
     * @param response response
     * @throws IOException
     */
    @GetMapping("/login/{type}")
    public void login(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    /**
     * 认证后回调
     *
     * @param type     类型
     * @param callback 回调对象
     * @param response response
     */
    @RequestMapping("/{type}/callback")
    public void login(@PathVariable String type, AuthCallback callback, HttpServletResponse response) {
        AuthRequest authRequest = factory.get(type);
        AuthResponse authResponse = authRequest.login(callback);
        // 认证成功，进行登录
        if (authResponse.ok()) {
            AuthUser user = (AuthUser) authResponse.getData();
            try {
                LoginUser loginUser = userInfoService.oauthLogin(user);
                // 如果配置了前段地址，则直接跳转
                if (StringUtils.hasText(frontHost)) {
                    frontHost = StringUtils.trimTrailingCharacter(frontHost, '/');
                    frontHost = StringUtils.trimTrailingCharacter(frontHost, '#');
                    frontHost = StringUtils.trimTrailingCharacter(frontHost, '/');
                    String token = loginUser.getToken();
                    String loginKey = systemLoginTokenService.createLoginKey(token);
                    response.sendRedirect(frontHost + "/#/successLogin?loginKey=" + loginKey);
                } else {
                    // 做页面跳转，主要是保存token
                    ResponseUtil.writeHtml(response, JwtUtil.getJumpPageHtml(loginUser.getToken(), successUrl));
                }
            } catch (Exception e) {
                ResponseUtil.writeHtml(response, e.getMessage());
            }
        } else {
            ResponseUtil.writeHtml(response, "oauth认证失败, msg:" + authResponse.getMsg());
        }
    }

    @GetMapping("token/get")
    @ResponseBody
    public Result<String> getToken(String loginKey) {
        Optional<String> optional = systemLoginTokenService.getTokenByLoginKey(loginKey);
        if (!optional.isPresent()) {
            throw new BizException("登录失败");
        }
        return Result.ok(optional.get());
    }

}