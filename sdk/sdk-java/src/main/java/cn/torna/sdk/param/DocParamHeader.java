package cn.torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocParamHeader {

    /** header名称 */
    private String name;

    /** 是否必须，1：是，0：否 */
    private Byte required;

    /** 示例值 */
    private String example;

    /** 描述 */
    private String description;

}
