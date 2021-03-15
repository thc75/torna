package cn.torna.sdk.result;

import cn.torna.sdk.common.TreeAware;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocParamResult implements TreeAware<DocParamResult, String> {
    private String id;

    /** 字段名称 */
    private String name;

    /** 字段类型 */
    private String type;

    /** 是否必须，1：是，0：否 */
    private Byte required;

    /** 最大长度 */
    private String maxLength;

    /** 示例值 */
    private String example;

    /** 描述 */
    private String description;

    /** 文档id */
    private String docId;

    /** 父节点 */
    private String parentId;

    /** 0：header, 1：请求参数，2：返回参数，3：错误码 */
    private Byte style;

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 创建时间 */
    private Date gmtCreate;

    /** 最后修改时间 */
    private Date gmtModified;

    /** 子节点 */
    private List<DocParamResult> children;

}
