package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：doc_info
 * 备注：文档信息
 *
 * @author tanghc
 */
@Table(name = "doc_info")
@Data
public class DocInfo {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 唯一id，md5(name:project_id:parent_id), 数据库字段：unique_id */
    private String uniqueId;

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

    /** 模块id，module.id, 数据库字段：module_id */
    private Long moduleId;

    /** 创建人, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建人, 数据库字段：modifier_id */
    private Long modifierId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}