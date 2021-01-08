package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：project_user
 * 备注：项目用户关系表
 *
 * @author tanghc
 */
@Table(name = "project_user")
@Data
public class ProjectUser {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** 角色，guest：访客，dev：开发者，admin：项目管理员, 数据库字段：role_code */
    private String roleCode;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}