package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：space_user
 * 备注：分组用户关系表
 *
 * @author tanghc
 */
@Table(name = "space_user")
@Data
public class SpaceUser {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** space.id, 数据库字段：space_id */
    private Long spaceId;

    /** 角色，guest：访客，dev：开发者，admin：空间管理员, 数据库字段：role_code */
    private String roleCode;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}