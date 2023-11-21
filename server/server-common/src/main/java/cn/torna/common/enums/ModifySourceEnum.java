package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 来源方式，0：推送，1：表单编辑, 2:文本编辑
 * @author thc
 */
@Getter
@AllArgsConstructor
public enum ModifySourceEnum {
    PUSH((byte)0),
    FORM((byte)1),
    TEXT((byte)2)
    ;

    private final byte source;

}
