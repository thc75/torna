package cn.torna.service.login.oauth;

import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;

/**
 * 解析用户
 * @author tanghc
 */
public interface AuthCustomResultParser {

    /**
     * 解析用户
     * @param source 用户来源
     * @param authToken 认证token
     * @param result 认证结果
     * @return 返回用户对象
     */
    AuthUser parse(String source, AuthToken authToken, JSONObject result);
}
