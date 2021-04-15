package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum MockRequestDataTypeEnum {

    KV((byte) 0),
    JSON((byte)1)
    ;

    MockRequestDataTypeEnum(byte type) {
        this.type = type;
    }

    public static MockRequestDataTypeEnum of(byte type) {
        MockRequestDataTypeEnum[] values = MockRequestDataTypeEnum.values();
        for (MockRequestDataTypeEnum value : values) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("error type of MockParamTypeEnum: " + type);
    }

    private final byte type;
}
