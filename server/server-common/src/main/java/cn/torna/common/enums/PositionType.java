package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变更位置
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum PositionType {
    DOC_NAME((byte) 0),
    DOC_DESCRIPTION((byte) 1),
    CONTENT_TYPE((byte) 2),
    DEPRECATED((byte) 3),
    PARAM_NAME((byte) 10),
    PARAM_TYPE((byte) 11),
    PARAM_REQUIRED((byte) 12),
    PARAM_MAXLENGTH((byte) 13),
    PARAM_DESCRIPTION((byte) 14),
    PARAM_EXAMPLE((byte) 15),
    ;
    private final byte type;

    public String getTypeName() {
        return name();
    }
}
