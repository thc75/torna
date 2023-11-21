package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum ModifyType {
    NONE((byte) -1, ""),

    UPDATE((byte) 0, "更新"),
    ADD((byte) 1, "新增"),
    DELETE((byte) 2, "删除"),
    ;
    private final byte type;
    private final String description;



}
