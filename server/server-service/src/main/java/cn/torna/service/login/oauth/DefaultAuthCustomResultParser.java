package cn.torna.service.login.oauth;

import cn.torna.common.bean.EnvironmentKeys;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;

/**
 * @author tanghc
 */
public class DefaultAuthCustomResultParser implements AuthCustomResultParser {
    @Override
    public AuthUser parse(String source, AuthToken authToken, JSONObject result) {
        return AuthUser.builder()
                .uuid(result.getString("id"))
                .username(result.getString(EnvironmentKeys.LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_USERNAME.getValue()))
                .nickname(result.getString(EnvironmentKeys.LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_NICKNAME.getValue()))
                .avatar(result.getString("avatar_url"))
                .blog(result.getString("web_url"))
                .company(result.getString("organization"))
                .location(result.getString("location"))
                .email(result.getString(EnvironmentKeys.LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_EMAIL.getValue()))
                .remark(result.getString("bio"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source)
                .build();
    }
}
