package cn.torna.dao.entity;

import java.util.Date;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;


/**
 * 表名：module_swagger_config
 * 备注：模块swagger配置
 *
 * @author tanghc
 */
@Table(name = "module_swagger_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ModuleSwaggerConfig {

    /** 主键id, 数据库字段：id */
    private Long id;

    /** module.id, 数据库字段：module_id */
    private Long moduleId;

    /** swagger文档url, 数据库字段：url */
    private String url;

    /** swagger文档内容, 数据库字段：content */
    private String content;

    /** 刷新时间间隔, 数据库字段：refresh_minutes */
    private Integer refreshMinutes;

    /** 刷新状态，1：启用，0：禁用 */
    private Byte refreshStatus;

    /** 认证用户名, 数据库字段：auth_username */
    private String authUsername;

    /** 认证密码, 数据库字段：auth_password */
    private String authPassword;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}