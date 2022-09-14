package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum ModifyType {
    UPDATE((byte) 0),
    ADD((byte) 1),
    DELETE((byte) 2),
    ;
    private final byte type;

}
