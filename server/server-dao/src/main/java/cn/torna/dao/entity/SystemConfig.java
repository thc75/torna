package cn.torna.dao.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：system_config
 *
 * @author tanghc
 */
@Table(name = "system_config")
@Data
public class SystemConfig {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**  数据库字段：config_key */
    private String configKey;

    /**  数据库字段：config_value */
    private String configValue;

    /**  数据库字段：remark */
    private String remark;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}