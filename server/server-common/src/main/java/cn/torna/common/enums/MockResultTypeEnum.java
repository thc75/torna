package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum MockResultTypeEnum {
    /**
     * 自定义
     */
    CUSTOM((byte) 0),
    /**
     * 脚本
     */
    SCRIPT((byte) 1),
    ;

    public static MockResultTypeEnum of(byte type) {
        MockResultTypeEnum[] values = MockResultTypeEnum.values();
        for (MockResultTypeEnum value : values) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("error type of MockResultTypeEnum: " + type);
    }

    private final byte type;

    MockResultTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
