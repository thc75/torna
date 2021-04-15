package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum ThirdPartyLoginTypeEnum {
    FORM("form"),
    OAUTH("oauth");
    private final String type;

    ThirdPartyLoginTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
