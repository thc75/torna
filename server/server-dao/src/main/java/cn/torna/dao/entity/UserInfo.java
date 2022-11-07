package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：user_info
 * 备注：用户表
 *
 * @author tanghc
 */
@Table(name = "user_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class UserInfo {

    /**  数据库字段：id */
    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 登录密码, 数据库字段：password */
    private String password;

    /** 昵称, 数据库字段：nickname */
    private String nickname;

    /** 是否是超级管理员, 数据库字段：is_super_admin */
    private Byte isSuperAdmin;

    /**  数据库字段：source */
    private String source;

    /**  数据库字段：email */
    private String email;

    /** 0：禁用，1：启用，2：重设密码, 数据库字段：status */
    private Byte status;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}