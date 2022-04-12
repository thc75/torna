package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocParamHeader implements IParam {

    /** header名称 */
    private String name;

    /** 是否必须，1：是，0：否 */
    private Byte required;

    /** 示例值 */
    private String example;

    /** 描述 */
    private String description;

}
