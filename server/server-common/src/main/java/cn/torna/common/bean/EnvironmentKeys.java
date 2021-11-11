package cn.torna.common.bean;

import cn.torna.common.context.EnvironmentContext;
import cn.torna.common.context.SpringContext;
import cn.torna.common.interfaces.IConfig;

public enum EnvironmentKeys {
    /** 是否开启注册 */
    REGISTER_ENABLE("torna.register.enable"),
    /** 是否开启第三方登录 */
    LOGIN_THIRD_PARTY_ENABLE("torna.login.third-party.enable"),
    /** LDAP url */
    TORNA_LDAP_URL("torna.ldap.url"),
    /** 第三方登录形式,form/oauth二选一 */
    LOGIN_THIRD_PARTY_TYPE("torna.login.third-party.type"),
    /** 第三方登录url，torna.login.third-party.type=form时有效 */
    LOGIN_THIRD_PARTY_FORM_URL("torna.login.third-party.form.url"),
    /** 登录用户名字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_PARAM_USERNAME("torna.login.third-party.form.key.param-username"),
    /** 登录密码字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_PARAM_PASSWORD("torna.login.third-party.form.key.param-password"),
    /** 登录返回结果昵称字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_RESULT_NICKNAME("torna.login.third-party.form.key.result-nickname"),
    /** 登录返回结果邮箱字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_RESULT_EMAIL("torna.login.third-party.form.key.result-email"),
    /** 登录返回结果code字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_RESULT_CODE("torna.login.third-party.form.key.result-code"),
    /** 登录返回结果msg字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_RESULT_MSG("torna.login.third-party.form.key.result-msg"),
    /** 登录返回结果data字段名称 */
    LOGIN_THIRD_PARTY_FORM_KEY_RESULT_DATA("torna.login.third-party.form.key.result-data"),
    /** 登录返回结果code成功时的值 */
    LOGIN_THIRD_PARTY_FORM_VALUE_RESULT_CODE("torna.login.third-party.form.value.result-code"),
    /** 登录请求时的content-type */
    LOGIN_THIRD_PARTY_FORM_CONTENT_TYPE("torna.login.third-party.form.content-type"),
    /** 登录请求时的method */
    LOGIN_THIRD_PARTY_FORM_METHOD("torna.login.third-party.form.method"),
    /** OAuth scope */
    LOGIN_THIRD_PARTY_OAUTH_SCOPE("torna.login.third-party.oauth.scope"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_USERNAME("torna.login.third-party.oauth.key.result-username"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_NICKNAME("torna.login.third-party.oauth.key.result-nickname"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_EMAIL("torna.login.third-party.oauth.key.result-email"),
    LOGIN_THIRD_PARTY_OAUTH_PARSER_CLASS_NAME("torna.login.third-party.oauth.parser-class-name", "cn.torna.service.login.oauth.DefaultAuthCustomResultParser"),

    /** 推送钉钉webhook */
    PUSH_DINGDING_WEBHOOK_URL("torna.push.dingding-webhook-url"),
    /** 变更文档内容模板 */
    PUSH_DINGDING_WEBHOOK_CONTENT("torna.push.dingding-webhook-content", "文档[%s]内容已变更")
    ;

    private final String key;
    private String defaultValue;

    EnvironmentKeys(String key) {
        this.key = key;
    }

    EnvironmentKeys(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(getValue());
    }

    public String getValue() {
        IConfig iConfig = SpringContext.getBean(IConfig.class);
        String config = iConfig.getConfig(this.key);
        if (config != null) {
            return config;
        }
        return getValue(defaultValue);
    }

    public String getKey() {
        return key;
    }

    public String getValue(String defaultValue) {
        return EnvironmentContext.getValue(key, defaultValue);
    }
}