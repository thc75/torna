package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum DocTypeEnum {
    HTTP((byte) 0),
    DUBBO((byte) 1),
    CUSTOM((byte) 2),
    ;

    DocTypeEnum(byte type) {
        this.type = type;
    }

    private final byte type;

    public byte getType() {
        return type;
    }
}
