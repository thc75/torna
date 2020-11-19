package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：project
 * 备注：项目表
 *
 * @author tanghc
 */
@Table(name = "project")
@Data
public class Project {

	/**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	/** 项目名称, 数据库字段：name */
    private String name;
    
	/** 项目描述, 数据库字段：description */
    private String description;
    
	/** 所属空间，space.id, 数据库字段：space_id */
    private Long spaceId;
    
	/** 是否私有项目，1：是，0：否, 数据库字段：is_private */
    private Byte isPrivate;
    
	/** 创建者userid, 数据库字段：creator_id */
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