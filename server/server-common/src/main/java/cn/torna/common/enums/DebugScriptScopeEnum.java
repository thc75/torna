package cn.torna.common.enums;

/**
 * 作用域，0：当前文档，1：当前应用，2：当前项目
 * @author tanghc
 */
public enum DebugScriptScopeEnum {
    /**
     * 当前文档
     */
    DOC((byte) 0),
    /**
     * 当前应用
     */
    MODULE((byte) 1),
    /**
     * 当前项目
     */
    PROJECT((byte)2)
    ;

    public static DebugScriptScopeEnum of(byte scope) {
        DebugScriptScopeEnum[] values = DebugScriptScopeEnum.values();
        for (DebugScriptScopeEnum value : values) {
            if (value.scope == scope) {
                return value;
            }
        }
        throw new IllegalArgumentException("scope error");
    }

    DebugScriptScopeEnum(byte scope) {
        this.scope = scope;
    }

    private final byte scope;

    public byte getScope() {
        return scope;
    }
}
