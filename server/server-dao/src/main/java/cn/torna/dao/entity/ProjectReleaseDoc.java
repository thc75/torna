package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Column;
import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：project_release_doc
 * 备注：项目版本文档关联表
 *
 * @author qiuyu
 */
@Table(name = "project_release_doc", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ProjectReleaseDoc {

    /**  数据库字段：id */
    private Long id;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** project_release.id */
    private Long releaseId;

    /** module.id */
    private Long moduleId;

    /** doc_info.id */
    private Long sourceId;

    /**  数据库字段：is_deleted */
    @Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}