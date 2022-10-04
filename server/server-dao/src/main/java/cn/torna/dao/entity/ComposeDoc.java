package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：compose_doc
 * 备注：文档引用
 *
 * @author tanghc
 */
@Table(name = "compose_doc", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ComposeDoc {

    /**  数据库字段：id */
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
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /**  数据库字段：order_index */
    private Integer orderIndex;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}