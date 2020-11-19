package torna.common.enums;

/**
 * 0：header, 1：请求参数，2：返回参数
 * @author tanghc
 */
public enum ParamStyleEnum {
    HEADER((byte)0),
    REQUEST((byte)1),
    RESPONSE((byte)2),
    ERROR_CODE((byte)3),
    ;

    private final byte style;

    ParamStyleEnum(byte style) {
        this.style = style;
    }

    public byte getStyle() {
        return style;
    }
}
