package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum UserInfoSourceEnum {
    REGISTER("register"),
    BACKEND("backend"),
    FORM("form"),
    OAUTH("oauth"),
    LDAP("ldap"),
    ;
    private final String source;

    UserInfoSourceEnum(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
