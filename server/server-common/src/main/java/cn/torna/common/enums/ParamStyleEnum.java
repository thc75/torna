package cn.torna.common.enums;

/**
 * 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码
 * @author tanghc
 */
public enum ParamStyleEnum {
    PATH((byte)0),
    HEADER((byte)1),
    QUERY((byte)5),
    REQUEST((byte)2),
    RESPONSE((byte)3),
    ERROR_CODE((byte)4),
    ;

    private final byte style;

    ParamStyleEnum(byte style) {
        this.style = style;
    }

    public byte getStyle() {
        return style;
    }
}
