package cn.torna.common.enums;

/**
 * 操作方式，0：人工操作，1：开放平台推送
 * @author tanghc
 */
public enum OperationMode {
    /**
     * 人工操作
     */
    MANUAL((byte)0),
    /**
     * 开放平台推送
     */
    OPEN((byte)1)

    ;

    private final byte type;

    OperationMode(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
