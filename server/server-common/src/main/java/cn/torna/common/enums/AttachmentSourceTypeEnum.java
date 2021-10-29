package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 附件来源枚举
 */
@AllArgsConstructor
@Getter
public enum AttachmentSourceTypeEnum {

    /**
     * 文档附件
     */
    DOC((byte) 0),

    /**
     * 评论附件
     */
    COMMENT((byte) 1),
    ;

    private final byte sourceType;

}
