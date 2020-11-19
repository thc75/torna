package torna.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class DocParamDTO {
    private Long id;

    /** 唯一id,md5(name+doc_id+parent_unique_id), 数据库字段：unique_id */
    private String uniqueId;

    /** 字段名称, 数据库字段：name */
    private String name;

    /** 字段类型, 数据库字段：type */
    private String type;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    private Byte required;

    /** 最大长度, 数据库字段：max_length */
    private String maxLength;

    /** 示例值, 数据库字段：example */
    private String example;

    /** 描述, 数据库字段：description */
    private String description;

    /** 参数枚举值,json数组格式，如：[{"code":"0",type:"string","msg":"已支付"}], 数据库字段：enum_content */
    private String enumContent;

    /** doc_info.id, 数据库字段：doc_id */
    private Long docId;

    /** 父节点, 数据库字段：parent_id */
    private Long parentId;

    /** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style */
    private Byte style;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /** 修改人, 数据库字段：modifier */
    private String modifier;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;
}
