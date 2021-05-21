package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：compose_doc
 * 备注：文档引用
 *
 * @author tanghc
 */
@Table(name = "compose_doc")
@Data
public class ComposeDoc {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** doc_info.id, 数据库字段：doc_id */
    private Long docId;

    /** compose_project.id, 数据库字段：project_id */
    private Long projectId;

    /** 是否文件夹, 数据库字段：is_folder */
    private Byte isFolder;

    /** 文件夹名称, 数据库字段：folder_name */
    private String folderName;

    /**  数据库字段：parent_id */
    private Long parentId;

    /** 文档来源, 数据库字段：origin */
    private String origin;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /**  数据库字段：order_index */
    private Integer orderIndex;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}