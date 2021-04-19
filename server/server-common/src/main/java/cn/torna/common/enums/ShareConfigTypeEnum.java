package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum ShareConfigTypeEnum {
    PUBLIC((byte)1),
    ENCRYPT((byte) 2),
    ;
    private final byte type;

    ShareConfigTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
