package torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocParamCreateParam {

    /** 字段名称  name */
    private String name;

    /** 字段类型  type */
    private String type;

    /** 是否必须，1：是，0：否  required */
    private Byte required;

    /** 最大长度  max_length */
    private String maxLength;

    /** 示例值  example */
    private String example;

    /** 描述  description */
    private String description;

    /** 字典id  */
    private String enumId;

    /** 父节点, 没有填空字符串  parent_id */
    private String parentId;

}
