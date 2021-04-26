package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum DocTypeEnum {
    HTTP((byte) 0),
    DUBBO((byte) 1),
    ;

    DocTypeEnum(byte protocol) {
        this.protocol = protocol;
    }

    private final byte protocol;

    public byte getProtocol() {
        return protocol;
    }
}
