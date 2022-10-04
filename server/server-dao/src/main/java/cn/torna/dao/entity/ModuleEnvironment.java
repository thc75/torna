package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：module_environment
 * 备注：模块调试环境
 *
 * @author tanghc
 */
@Table(name = "module_environment", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ModuleEnvironment {

    /**  数据库字段：id */
    private Long id;

    /** module.id, 数据库字段：module_id */
    private Long moduleId;

    /** 环境名称, 数据库字段：name */
    private String name;

    /** 调试路径, 数据库字段：url */
    private String url;

    /** 是否公开, 数据库字段：is_public */
    private Byte isPublic;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}