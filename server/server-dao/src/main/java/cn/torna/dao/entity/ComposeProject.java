package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：compose_project
 * 备注：组合项目表
 *
 * @author tanghc
 */
@Table(name = "compose_project")
@Data
public class ComposeProject {

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

    /** 访问形式，1：公开，2：加密, 数据库字段：type */
    private Byte type;

    /** 访问密码, 数据库字段：password */
    private String password;

    /** 创建者userid, 数据库字段：creator_id */
    private Long creatorId;

    /**  数据库字段：creator_name */
    private String creatorName;

    /**  数据库字段：modifier_id */
    private Long modifierId;

    /**  数据库字段：modifier_name */
    private String modifierName;

    /** 排序索引, 数据库字段：order_index */
    private Integer orderIndex;

    /** 1：有效，0：无效, 数据库字段：status */
    private Byte status;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}