package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：system_config
 * 备注：系统配置表
 *
 * @author tanghc
 */
@Table(name = "system_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class SystemConfig {

    /**  数据库字段：id */
    private Long id;

    /**  数据库字段：config_key */
    private String configKey;

    /**  数据库字段：config_value */
    private String configValue;

    /**  数据库字段：remark */
    private String remark;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}