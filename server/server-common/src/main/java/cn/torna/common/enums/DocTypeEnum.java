package cn.torna.common.enums;

import java.util.Objects;

/**
 * @author tanghc
 */
public enum DocTypeEnum {
    HTTP((byte) 0),
    DUBBO((byte) 1),
    /** 富文本 */
    CUSTOM((byte) 2),
    /** markdown */
    MARKDOWN((byte) 3),
    ;

    DocTypeEnum(byte type) {
        this.type = type;
    }

    public static DocTypeEnum of(Byte type) {
        for (DocTypeEnum value : DocTypeEnum.values()) {
            if (Objects.equals(value.getType(), type)) {
                return value;
            }
        }
        return HTTP;
    }

    private final byte type;

    public byte getType() {
        return type;
    }
}
