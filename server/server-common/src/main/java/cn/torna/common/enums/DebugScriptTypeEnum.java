package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum DebugScriptTypeEnum {
    /**
     * 前置脚本
     */
    PRE((byte) 0),
    /**
     * 后置脚本
     */
    AFTER((byte)1)
    ;

    DebugScriptTypeEnum(byte type) {
        this.type = type;
    }

    private final byte type;

    public byte getType() {
        return type;
    }
}
