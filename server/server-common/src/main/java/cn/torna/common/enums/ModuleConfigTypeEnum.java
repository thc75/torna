package cn.torna.common.enums;

/**
 * 模块类型，0：通用配置，1：全局header
 *
 * @author tanghc
 */
public enum ModuleConfigTypeEnum {
    /**
     * 通用配置
     */
    COMMON((byte) 0),
    /**
     * 全局header
     */
    GLOBAL_HEADERS((byte) 1),
    /**
     * 调试host
     */
    DEBUG_HOST((byte) 2),
    /**
     * 全局参数
     */
    GLOBAL_PARAMS((byte) 3),
    /**
     * 全局返回参数
     */
    GLOBAL_RETURNS((byte) 4),
    ;

    private final byte type;

    ModuleConfigTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
