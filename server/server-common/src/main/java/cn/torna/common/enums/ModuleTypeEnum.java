package cn.torna.common.enums;

/**
 * 模块类型，0：自定义添加，1：swagger导入，2：postman导入
 *
 * @author tanghc
 */
public enum ModuleTypeEnum {
    /**
     * 自定义添加
     */
    CUSTOM_ADD((byte) 0),
    /**
     * swagger导入
     */
    SWAGGER_IMPORT((byte) 1),
    /**
     * postman导入
     */
    POSTMAN_IMPORT((byte) 2),
    ;

    private final byte type;

    ModuleTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
