package cn.torna.service.login.form;

/**
 * @author tanghc
 */
public interface ThirdPartyLoginManager {

    /**
     * 第三方登录
     * @param loginForm 登录表单
     * @return 返回登录结果
     */
    LoginResult login(LoginForm loginForm) throws Exception;
}
