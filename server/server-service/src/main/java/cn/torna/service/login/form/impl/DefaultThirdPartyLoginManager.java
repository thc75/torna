package cn.torna.service.login.form.impl;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.HttpHelper;
import cn.torna.common.enums.ThirdPartyLoginTypeEnum;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.exception.BizException;
import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.ThirdPartyLoginManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 第三方登录，模拟表单提交
 * @author tanghc
 */
@Service
public class DefaultThirdPartyLoginManager implements ThirdPartyLoginManager {

    @Override
    public LoginResult login(LoginForm loginForm) throws IOException {
        String type = EnvironmentKeys.LOGIN_THIRD_PARTY_TYPE.getValue();
        if (!ThirdPartyLoginTypeEnum.FORM.getType().equals(type)) {
            throw new BizException("torna.login.third-party.type不正确");
        }
        // 登录
        HttpHelper.ResponseResult responseResult = this.requestForm(loginForm);
        int status = responseResult.getStatus();
        if (status != HttpStatus.SC_OK) {
            throw new RuntimeException("请求失败，status:" + status);
        }
        String body = responseResult.asString();
        return parseResult(loginForm, body);
    }

    /**
     * 解析登录返回结果
     * @param loginForm 登录对象
     * @param body 返回内容
     * @return 返回封装好的对象
     */
    protected LoginResult parseResult(LoginForm loginForm, String body) {
        // 解析登录返回结果
        JSONObject result = JSON.parseObject(body);

        String codeKey = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_RESULT_CODE.getValue();
        String successValue = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_VALUE_RESULT_CODE.getValue();
        if (StringUtils.hasText(codeKey) && !Objects.equals(successValue, result.getString(codeKey))) {
            String msgKey = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_RESULT_MSG.getValue();
            String msg = result.getString(msgKey);
            throw new BizException("登录失败，" + msg);
        }
        String nicknameKey = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_RESULT_NICKNAME.getValue();
        String emailKey = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_RESULT_EMAIL.getValue();
        // 获取数据节点
        JSONObject data = buildData(result);
        String username = loginForm.getUsername();
        String nickname = data.getString(nicknameKey);
        if (nickname == null) {
            nickname = username;
        }
        String email = data.getString(emailKey);
        LoginResult loginResult = new LoginResult();
        loginResult.setUsername(username);
        loginResult.setNickname(nickname);
        loginResult.setEmail(email);
        loginResult.setUserInfoSourceEnum(UserInfoSourceEnum.FORM);
        return loginResult;
    }

    protected JSONObject buildData(JSONObject result) {
        String dataKey = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_RESULT_DATA.getValue();
        if (StringUtils.isEmpty(dataKey)) {
            return result;
        }
        /*
        {
            "data": {
                "user": {
                    "username": "Jim"
                }
            }
        }
         */
        // torna.login.third-party.form.key.result-data=data.user
        String[] arr = dataKey.split("\\.");
        JSONObject data = result;
        for (String key : arr) {
            data = data.getJSONObject(key);
        }
        return data;
    }

    protected HttpHelper.ResponseResult requestForm(LoginForm loginForm) throws IOException {
        String url = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_URL.getValue();
        String contentType = EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_CONTENT_TYPE.getValue();
        String contentTypeLower = contentType.toLowerCase();
        Map<String, String> params = new HashMap<>(8);
        params.put(EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_PARAM_USERNAME.getValue(), loginForm.getUsername());
        params.put(EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_KEY_PARAM_PASSWORD.getValue(), loginForm.getPassword());
        HttpHelper httpHelper;
        if (contentTypeLower.contains("x-www-form-urlencoded")) {
            httpHelper = HttpHelper.postForm(url, params);
        } else if (contentTypeLower.contains("json")) {
            String text = JSON.toJSONString(params);
            httpHelper = HttpHelper.postJson(url, text);
        } else {
            httpHelper = HttpHelper
                    .create()
                    .url(url)
                    .method(EnvironmentKeys.LOGIN_THIRD_PARTY_FORM_METHOD.getValue());
        }
        return httpHelper.execute();
    }

}
