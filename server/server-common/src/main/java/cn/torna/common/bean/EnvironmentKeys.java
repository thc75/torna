package cn.torna.common.bean;

import cn.torna.common.enums.DocSortType;
import cn.torna.common.enums.DocStatusEnum;

import java.util.Objects;

/**
 * 获取环境配置信息，读取顺序：缓存>数据库>Spring Environment
 */
public enum EnvironmentKeys {
    TORNA_FRONT_URL("torna.front-url"),
    /** 是否开启注册 */
    REGISTER_ENABLE("torna.register.enable"),
    /** 是否开启第三方登录 */
    LOGIN_THIRD_PARTY_ENABLE("torna.login.third-party.enable"),
    /** LDAP url */
    TORNA_LDAP_URL("torna.ldap.url"),
    TORNA_LDAP_CUSTOM_URL("torna.ladp.custom-url"),
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
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_ID("torna.login.third-party.oauth.key.result-id"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_USERNAME("torna.login.third-party.oauth.key.result-username"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_NICKNAME("torna.login.third-party.oauth.key.result-nickname"),
    LOGIN_THIRD_PARTY_OAUTH_KEY_RESULT_EMAIL("torna.login.third-party.oauth.key.result-email"),
    LOGIN_THIRD_PARTY_OAUTH_PARSER_CLASS_NAME("torna.login.third-party.oauth.parser-class-name", "cn.torna.service.login.oauth.DefaultAuthCustomResultParser"),

    /** 推送钉钉webhook */
    PUSH_DINGDING_WEBHOOK_URL("torna.push.dingding-webhook-url"),

    PUSH_DINGDING_WECOM_URL("torna.push.wecom-webhook-url"),
    /** 变更文档内容模板 */
    PUSH_DINGDING_WEBHOOK_CONTENT("torna.push.dingding-webhook-content", "【文档{modifyType}提醒】\n" +
            "【所属应用】： {projectName} - {appName}\n" +
            "【文档名称】：{docName}\n" +
            "【修改人】：{modifier}\n" +
            "【修改时间】：{modifyTime}\n" +
            "【查看地址】：{docViewUrl}\n" +
            "{@user}"),
    PUSH_WECOM_WEBHOOK_CONTENT("torna.push.weCom-webhook-content", "【文档{modifyType}提醒】\n" +
            "【所属应用】： {projectName} - {appName}\n" +
            "【文档名称】：{docName}\n" +
            "【修改人】：{modifier}\n" +
            "【修改时间】：{modifyTime}\n" +
            "【查看地址】：{docViewUrl}"),
    /** 是否打印推送内容，默认false */
    TORNA_PUSH_PRINT_CONTENT("torna.push.print-content", "false"),
    /** 是否允许相同的文件夹，默认：true */
    TORNA_PUSH_ALLOW_SAME_FOLDER("torna.push.allow-same-folder", "true"),
    /** 保存不同文档处理数量 */
    TORNA_SCHEDULE_SAVE_DOC_DIFF_POLL_SIZE("torna.schedule.save-doc-diff.poll-size", "20"),
    /** 文档排序规则 */
    TORNA_DOC_SORT_TYPE("torna.doc-sort-type", DocSortType.BY_ORDER.getType()),
    /** 上传文件域名 */
    TORNA_UPLOAD_DOMAIN("torna.upload.domain", null),
    TORNA_UPLOAD_DIR("torna.upload.dir", null),
    /** 推送是否覆盖 */
    TORNA_PUSH_OVERRIDE("torna.push.override", String.valueOf(false)),
    /** 文档推送后默认状态 */
    TORNA_PUSH_DOC_DEFAULT_STATUS("torna.push.doc-default-status", String.valueOf(DocStatusEnum.DONE.getStatus())),
    /** 系统默认语言 */
    TORNA_DEFAULT_LANG("torna.default-lang", "zh-CN"),
    TORNA_NAME_VERSION_TPL("torna.name-version-tpl", "{_name_} {_version_}"),
    /** 是否开启对接MeterSphere */
    ENABLE_METER_SPHERE("metershpere.enable", "false"),
    /** torna推送处理器数量 */
    TORNA_PUSH_PROCESS_NUM("torna.push-process-num", "4"),
    TORNA_PUSH_EXECUTE_SIZE("torna.push-execute-size", "50"),
    /** 快照数量 */
    TORNA_SNAPSHOT_SIZE("torna.snapshot-size", "5"),

    TORNA_METERSPHERE_MODULE_MAXLEVEL("torna.metersphere.module.maxLevel", "2"),
    ;

    private final String key;
    private String defaultValue;

    public static EnvironmentKeys of(String key) {
        for (EnvironmentKeys value : EnvironmentKeys.values()) {
            if (Objects.equals(value.key, key)) {
                return value;
            }
        }
        return null;
    }

    EnvironmentKeys(String key) {
        this.key = key;
    }

    EnvironmentKeys(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(getValue());
    }

    public int getInt() {
        return Integer.parseInt(getValue());
    }

    public String getValue() {
        return getValue(defaultValue);
    }

    public String getKey() {
        return key;
    }

    public String getValue(String defaultValue) {
        return Configs.getValue(key, defaultValue);
    }
}
