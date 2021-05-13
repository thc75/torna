package cn.torna.web.controller.system;

import cn.torna.common.bean.HttpHelper;
import cn.torna.common.bean.LoginUser;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.DingTalkSignUtil;
import cn.torna.common.util.JwtUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.service.SystemConfigService;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.DingTalkLoginDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉免登地方网站
 * https://developers.dingtalk.com/document/app/logon-free-third-party-websites
 */
@Slf4j
@Controller
@RequestMapping("system/dingtalk")
public class DingTalkController implements InitializingBean {

    private static final String LOGIN_TEMPLATE = "%s?appid=%s&response_type=code&scope=snsapi_auth&redirect_uri=%s";
    public static final String DINGTALK_ACCESS_TOKEN_KEY = "dingtalk.access_token";

    @Value("${torna.dingtalk.app-key}")
    private String appKey;

    @Value("${torna.dingtalk.app-secret}")
    private String appSecret;

    @Value("${torna.dingtalk.redirect-uri}")
    private String redirectUri;

    @Value("${torna.dingtalk.authorize-url}")
    private String authorizeUrl;

    @Value("${torna.dingtalk.open-url.getuserinfo_bycode}")
    private String getuserinfo_bycodeUrl;

    @Value("${torna.dingtalk.open-url.getbyunionid}")
    private String getbyunionidUrl;

    @Value("${torna.dingtalk.open-url.gettoken}")
    private String gettokenUrl;

    @Value("${torna.dingtalk.open-url.userget}")
    private String usergetUrl;

    @Value("${torna.dingtalk.success-url:/}")
    private String successUrl;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SystemConfigService systemConfigService;

    private static volatile TokenBean tokenBean;

    /**
     * 应用登录，此链接处理成功后，会重定向跳转到指定的redirect_uri，并向url追加临时授权码code及state两个参数。
     * @param response response
     */
    @RequestMapping("login")
    public void login(HttpServletResponse response) {
        String url = String.format(LOGIN_TEMPLATE, authorizeUrl, appKey, UriUtils.encode(redirectUri, StandardCharsets.UTF_8));
        log.debug("钉钉认证url:{}", url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("登录失败，url：{}", url, e);
            ResponseUtil.writeText(response, "登录失败");
        }
    }

    /**
     * 服务端通过临时授权码获取授权用户的个人信息。
     * @param code
     */
    @RequestMapping("callback")
    public void callback(@RequestParam String code, HttpServletResponse response) {
        try {
            // 调用sns/getuserinfo_bycode接口获取用户信息
            OpenInfoResult openInfoResult = getOpenInfo(code);
            // 根据unionid获取userid。
            TokenBean tokenBean = this.getToken();
            String access_token = tokenBean.getAccessToken();
            UserIdResult userIdResult = this.getUserId(access_token, openInfoResult.getUnionid());
            // 根据userid获取用户详情。
            UserDetailResult userDetail = this.getUserDetail(access_token, userIdResult.getUserid());
            DingTalkLoginDTO dingTalkLoginDTO = buildDingTalkLoginDTO(openInfoResult, userDetail);
            // 登录
            LoginUser loginUser = userInfoService.dingtalkLogin(dingTalkLoginDTO);
            // 做页面跳转，主要是保存token
            ResponseUtil.writeHtml(response, JwtUtil.getJumpPageHtml(loginUser.getToken(), successUrl));
        } catch (Exception e) {
            log.error("登录异常", e);
            ResponseUtil.writeText(response, "登录异常");
        }
    }

    private DingTalkLoginDTO buildDingTalkLoginDTO(OpenInfoResult openInfoResult, UserDetailResult userDetail) {
        DingTalkLoginDTO dingTalkLoginDTO = new DingTalkLoginDTO();
        CopyUtil.copyPropertiesIgnoreNull(openInfoResult, dingTalkLoginDTO);
        CopyUtil.copyPropertiesIgnoreNull(userDetail, dingTalkLoginDTO);
        return dingTalkLoginDTO;
    }

    private OpenInfoResult getOpenInfo(String code) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = DingTalkSignUtil.getSignature(appSecret, timestamp);
        Map<String, String> data = new HashMap<>(4);
        data.put("tmp_auth_code", code);
        String json = JSON.toJSONString(data);
        String result = HttpHelper.postJson(getuserinfo_bycodeUrl, json)
                    .parameter("accessKey", appKey)
                    .parameter("timestamp", timestamp)
                    .parameter("signature", signature)
                    .execute()
                    .asString();
        JSONObject jsonObject = parseResult(result);
        JSONObject userInfo = jsonObject.getJSONObject("user_info");
        return userInfo.toJavaObject(OpenInfoResult.class);
    }

    /**
     * 获取accessToken
     * @return
     * @throws IOException
     */
    private TokenBean getToken() throws IOException {
        // token不存在或过期
        if (tokenBean == null || tokenBean.getExpireTime() < System.currentTimeMillis()) {
            String result = HttpHelper.get(gettokenUrl)
                    .parameter("appkey", appKey)
                    .parameter("appsecret", appSecret)
                    .execute()
                    .asString();
            JSONObject jsonObject = parseResult(result);
            TokenResult tokenResult = jsonObject.toJavaObject(TokenResult.class);
            long expireTime = tokenResult.getExpires_in() * 1000 + System.currentTimeMillis();
            if (tokenBean == null) {
                tokenBean = new TokenBean();
            }
            tokenBean.setAccessToken(tokenResult.getAccess_token());
            tokenBean.setExpireTime(expireTime);
            // token保存到数据库
            systemConfigService.setConfig(DINGTALK_ACCESS_TOKEN_KEY, JSON.toJSONString(tokenBean));
            return tokenBean;
        }
        return tokenBean;
    }

    /**
     * 获取userId
     * @param accessToken accessToken
     * @param unionid unionid
     * @return
     * @throws IOException
     */
    private UserIdResult getUserId(String accessToken, String unionid) throws IOException {
        Map<String, String> data = new HashMap<>(4);
        data.put("unionid", unionid);
        String result = HttpHelper.postJson(getbyunionidUrl, JSON.toJSONString(data))
                .parameter("access_token", accessToken)
                .execute()
                .asString();
        JSONObject jsonObject = parseResult(result);
        JSONObject userIdInfoData = jsonObject.getJSONObject("result");
        return userIdInfoData.toJavaObject(UserIdResult.class);
    }

    /**
     * 根据userid获取用户详情
     * @param accessToken
     * @param userid
     * @return
     */
    private UserDetailResult getUserDetail(String accessToken, String userid) throws IOException {
        String result = HttpHelper.get(usergetUrl)
                .parameter("access_token", accessToken)
                .parameter("userid", userid)
                .execute()
                .asString();
        JSONObject jsonObject = parseResult(result);
        JSONObject dataNode = jsonObject.getJSONObject("result");
        return dataNode.toJavaObject(UserDetailResult.class);
    }

    private static JSONObject parseResult(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        checkResult(jsonObject);
        return jsonObject;
    }

    private static void checkResult(JSONObject jsonObject) {
        Assert.isTrue("0".equals(jsonObject.getString("errcode")),
                "请求失败, errmsg：" + jsonObject.getString("errmsg"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String configValue = systemConfigService.getConfigValue(DINGTALK_ACCESS_TOKEN_KEY, null);
        if (configValue != null) {
            tokenBean = JSON.parseObject(configValue, TokenBean.class);
        }
    }

    @Data
    private static class TokenBean {
        private String accessToken;
        /**
         * 过期时间
         */
        private long expireTime;
    }

    @Data
    private static class UserIdResult {
        /**
         * 联系类型：
         * 0：企业内部员工
         * 1：企业外部联系人
         */
        private Integer contact_type;
        /**
         * 用户的userid。
         */
        private String userid;

    }

    @Data
    private static class TokenResult {
        private String access_token;
        /**
         * access_token的过期时间，单位秒。
         */
        private Integer expires_in;
    }

    @Data
    private static class OpenInfoResult {
        private String nick;
        private String unionid;
        private String openid;
    }

    @Data
    private static class UserDetailResult {
        private String userid;
        private String unionid;
        private String name;
        private String email;
        private Boolean boss;
    }

}