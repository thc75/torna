package cn.torna.common.enums;

import java.util.Objects;

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

    public static UserInfoSourceEnum of(String source) {
        for (UserInfoSourceEnum value : UserInfoSourceEnum.values()) {
            if (Objects.equals(source, value.source)) {
                return value;
            }
        }
        return UserInfoSourceEnum.REGISTER;
    }

    private final String source;

    UserInfoSourceEnum(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
