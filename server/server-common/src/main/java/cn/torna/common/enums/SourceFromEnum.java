package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 来源方式，0：推送，1：表单编辑
 * @author thc
 */
@Getter
@AllArgsConstructor
public enum SourceFromEnum {
    PUSH((byte)0),
    FORM((byte)1);

    private final byte source;

}
