package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：share_config
 * 备注：分享配置表
 *
 * @author tanghc
 */
@Table(name = "share_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ShareConfig {

    /**  数据库字段：id */
    private Long id;

    /** 分享形式，1：公开，2：加密, 数据库字段：type */
    private Byte type;

    /** 密码, 数据库字段：password */
    private String password;

    /** 状态，1：有效，0：无效, 数据库字段：status */
    private Byte status;

    /** module.id, 数据库字段：module_id */
    private Long moduleId;

    /** 是否为全部文档, 数据库字段：is_all */
    private Byte isAll;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**
     * 调试环境是否全选， 1-全选， 0-不选
     */
    private Byte isAllSelectedDebug;

    /** 备注, 数据库字段：remark */
    private String remark;

    /** 创建人, 数据库字段：creator_name */
    private String creatorName;

    /** 是否显示调试, 数据库字段：is_show_debug */
    private Byte isShowDebug;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}