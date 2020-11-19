package torna.web.controller.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocInfoVO {
    private Long id;

    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档概述, 数据库字段：description */
    private String description;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    /** 父节点, 数据库字段：parent_id */
    private Long parentId;

    /** 项目id，project.id, 数据库字段：project_id */
    private Long projectId;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /** 修改人, 数据库字段：modifier */
    private String modifier;
}
