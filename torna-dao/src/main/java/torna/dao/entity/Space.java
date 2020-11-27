package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：space
 * 备注：分组表
 *
 * @author tanghc
 */
@Table(name = "space")
@Data
public class Space {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 空间名称, 数据库字段：name */
    private String name;

    /** 创建者userid, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建者userid, 数据库字段：modifier_id */
    private Long modifierId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}