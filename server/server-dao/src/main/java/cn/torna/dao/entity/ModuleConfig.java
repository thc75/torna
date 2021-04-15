package cn.torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：module_config
 * 备注：系统配置
 *
 * @author tanghc
 */
@Table(name = "module_config")
@Data
public class ModuleConfig {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**  数据库字段：module_id */
    private Long moduleId;

    /** 配置类型，1：全局header, 数据库字段：type */
    private Byte type;

    /** 配置key, 数据库字段：config_key */
    private String configKey;

    /** 配置值, 数据库字段：config_value */
    private String configValue;

    /** 扩展ID, 数据库字段：extend_id */
    private Long extendId;

    /** 描述, 数据库字段：description */
    private String description;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}