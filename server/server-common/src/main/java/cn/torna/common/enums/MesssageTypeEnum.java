package cn.torna.common.enums;

/**
 * @author tanghc
 */
public enum MesssageTypeEnum {
    /**
     * 订阅文档
     */
    DOC((byte)1),
    /**
     * 订阅项目
     */
    PROJECT((byte)2),
    /**
     * 推送文档
     */
    PUSH_DOC((byte) 3),
    ;

    private final byte type;

    MesssageTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
