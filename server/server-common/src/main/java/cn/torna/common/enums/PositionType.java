package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变更位置
 *
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum PositionType {
    DOC_NAME((byte) 0),
    DOC_HTTP_METHOD((byte) 1),
    DOC_URL((byte) 2),
    CONTENT_TYPE((byte) 3),
    DOC_DESCRIPTION((byte) 4),
    DEPRECATED((byte) 5),
    IS_SHOW((byte) 6),
    ORDER_INDEX((byte) 7),
    STATUS((byte) 13),
    AUTHOR((byte) 14),
    USE_GLOBAL_HEADERS((byte) 15),
    USE_GLOBAL_PARAMS((byte) 16),
    USE_GLOBAL_RETURNS((byte) 17),
    IS_REQUEST_ARRAY((byte) 18),
    IS_RESPONSE_ARRAY((byte) 19),
    REQUEST_ARRAY_TYPE((byte) 20),
    RESPONSE_ARRAY_TYPE((byte) 21),

    PATH_PARAM((byte) 40),
    HEADER_PARAM((byte) 41),
    QUERY_PARAM((byte) 42),
    REQUEST_PARAM((byte) 43),
    RESPONSE_PARAM((byte) 44),

    PARAM_NAME((byte) 50),
    PARAM_TYPE((byte) 51),
    PARAM_REQUIRED((byte) 52),
    PARAM_MAXLENGTH((byte) 53),
    PARAM_DESCRIPTION((byte) 54),
    PARAM_EXAMPLE((byte) 55),
    ;
    private final byte type;

    public String getTypeName() {
        return name();
    }
}
