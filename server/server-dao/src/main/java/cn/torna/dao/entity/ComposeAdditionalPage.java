package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：compose_additional_page
 * 备注：聚合文档附加页
 *
 * @author tanghc
 */
@Table(name = "compose_additional_page", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ComposeAdditionalPage {

    /**  数据库字段：id */
    private Long id;

    /** compose_project.id, 数据库字段：project_id */
    private Long projectId;

    /** 文档标题, 数据库字段：title */
    private String title;

    /** 文档内容, 数据库字段：content */
    private String content;

    /** 排序值, 数据库字段：order_index */
    private Integer orderIndex;

    /** 1:启用，0：禁用, 数据库字段：status */
    private Byte status;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}