package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：module
 * 备注：项目模块
 *
 * @author tanghc
 */
@Table(name = "module")
@Data
public class Module {

	/**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	/** 模块名称, 数据库字段：name */
    private String name;
    
	/** project.id, 数据库字段：project_id */
    private Long projectId;
    
	/** 模块类型，0：swagger导入，1：自定义添加，2：postman导入, 数据库字段：type */
    private Byte type;
    
	/** 导入url, 数据库字段：import_url */
    private String importUrl;
    
	/** basic认证用户名, 数据库字段：basic_auth_username */
    private String basicAuthUsername;
    
	/** basic认证密码, 数据库字段：basic_auth_password */
    private String basicAuthPassword;
    
	/**  数据库字段：creator_id */
    private Long creatorId;
    
	/**  数据库字段：modifier_id */
    private Long modifierId;
    
	/**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;
    
	/**  数据库字段：gmt_create */
    private Date gmtCreate;
    
	/**  数据库字段：gmt_modified */
    private Date gmtModified;
    

}