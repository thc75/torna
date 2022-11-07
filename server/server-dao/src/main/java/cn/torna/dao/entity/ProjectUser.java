package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：project_user
 * 备注：项目用户关系表
 *
 * @author tanghc
 */
@Table(name = "project_user", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ProjectUser {

    /**  数据库字段：id */
    private Long id;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** 角色，guest：访客，dev：开发者，admin：项目管理员, 数据库字段：role_code */
    private String roleCode;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}