package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum PropTypeEnum {

    DOC_INFO_PROP((byte) 0),
    ;

    PropTypeEnum(byte type) {
        this.type = type;
    }

    private final byte type;

    public byte getType() {
        return type;
    }
}
