package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：open_user
 * 备注：开放用户
 *
 * @author tanghc
 */
@Table(name = "open_user")
@Data
public class OpenUser {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** appKey, 数据库字段：app_key */
    private String appKey;

    /** secret, 数据库字段：secret */
    private String secret;

    /** 1启用，0禁用, 数据库字段：status */
    private Byte status;

    /** 备注, 数据库字段：remark */
    private String remark;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}