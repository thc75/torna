package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum UserStatusEnum {
    DISABLED((byte)0),
    ENABLE((byte)1),
    SET_PASSWORD((byte)2),
    ;

    private final byte status;

    UserStatusEnum(byte style) {
        this.status = style;
    }

    public static UserStatusEnum of(int status) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.status == status) {
                return value;
            }
        }
        return DISABLED;
    }

    public byte getStatus() {
        return status;
    }
}
