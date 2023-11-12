package cn.torna.common.message;

/**
 *
 * @author tanghc
 */
public enum MessageEnum {
    DOC_UPDATE("message.doc.update"),
    DOC_UPDATE_REMARK("message.doc.update-remark"),
    DOC_DELETE("message.doc.delete"),
    PUSH_ERROR("message.doc.push"),
    PUSH_DOC_SUCCESS("message.custom"),

    ;
    private final MessageMeta messageMeta;

    MessageEnum(String key) {
        this.messageMeta = new MessageMeta(key);
    }

    public MessageMeta getMessageMeta() {
        return messageMeta;
    }

}
