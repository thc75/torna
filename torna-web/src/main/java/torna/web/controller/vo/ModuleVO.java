package torna.web.controller.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleVO {
    private Long id;

    /** 模块名称, 数据库字段：name */
    private String name;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** 模块类型，0：自定义添加，1：swagger导入，2：postman导入, 数据库字段：type */
    private Byte type;

    private String importUrl;
}
