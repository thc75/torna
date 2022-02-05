package cn.torna.common.enums;

/**
 * 模块类型
 *
 * @author tanghc
 */
public enum ModuleConfigTypeEnum {
    /**
     * 通用配置
     */
    COMMON((byte) 0, (byte) 0),
    /**
     * 公共请求头
     */
    GLOBAL_HEADERS((byte) 1, ParamStyleEnum.HEADER.getStyle()),
    /**
     * 调试host
     */
    DEBUG_HOST((byte) 2, (byte)0),
    /**
     * 公共请求参数
     */
    GLOBAL_PARAMS((byte) 3, ParamStyleEnum.REQUEST.getStyle()),
    /**
     * 公共返回参数
     */
    GLOBAL_RETURNS((byte) 4, ParamStyleEnum.RESPONSE.getStyle()),
    /**
     * 公共错误码
     */
    GLOBAL_ERROR_CODES((byte) 5, ParamStyleEnum.ERROR_CODE.getStyle()),
    ;

    private final byte type;
    private final byte style;

    ModuleConfigTypeEnum(byte type, byte style) {
        this.type = type;
        this.style = style;
    }

    public byte getType() {
        return type;
    }

    public byte getStyle() {
        return style;
    }
}
