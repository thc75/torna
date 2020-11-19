package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocItemCreateDTO {
    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档描述, 数据库字段：description */
    private String description;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    /** 父节点, 数据库字段：parent_id */
    private Long parentId;

    private Long moduleId;

}
