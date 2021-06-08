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
    COMMON((byte) 0),
    /**
     * 公共请求头
     */
    GLOBAL_HEADERS((byte) 1),
    /**
     * 调试host
     */
    DEBUG_HOST((byte) 2),
    /**
     * 公共请求参数
     */
    GLOBAL_PARAMS((byte) 3),
    /**
     * 公共返回参数
     */
    GLOBAL_RETURNS((byte) 4),
    /**
     * 公共错误码
     */
    GLOBAL_ERROR_CODES((byte) 5),
    ;

    private final byte type;

    ModuleConfigTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
