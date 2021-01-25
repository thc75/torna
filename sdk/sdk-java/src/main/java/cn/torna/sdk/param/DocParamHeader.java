package cn.torna.sdk.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocParamHeader {

    /** header名称 name */
    private String name;

    /** 是否必须，1：是，0：否 required */
    private Byte required;

    /** 示例值 example */
    private String example;

    /** 描述 description */
    private String description;

}
