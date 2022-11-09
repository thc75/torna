package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 修改方式，0：推送，1：表单编辑
 * @author thc
 */
@Getter
@AllArgsConstructor
public enum DocDiffModifySourceEnum {
    PUSH((byte)0),
    FORM((byte)1);

    private final byte source;

}
