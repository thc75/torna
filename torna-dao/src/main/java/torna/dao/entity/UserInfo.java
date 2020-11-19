package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：user_info
 * 备注：用户表
 *
 * @author tanghc
 */
@Table(name = "user_info")
@Data
public class UserInfo {

	/**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	/** 登录账号/邮箱, 数据库字段：username */
    private String username;
    
	/** 登录密码, 数据库字段：password */
    private String password;
    
	/** 真实姓名, 数据库字段：realname */
    private String realname;
    
	/** 是否是管理员, 数据库字段：is_admin */
    private Byte isAdmin;
    
	/**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;
    
	/**  数据库字段：gmt_create */
    private Date gmtCreate;
    
	/**  数据库字段：gmt_modified */
    private Date gmtModified;
    

}