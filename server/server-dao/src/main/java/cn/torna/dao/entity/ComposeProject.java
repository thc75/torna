package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：compose_project
 * 备注：组合项目表
 *
 * @author tanghc
 */
@Table(name = "compose_project", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class ComposeProject {

    /**  数据库字段：id */
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

    /** 是否显示调试, 数据库字段：show_debug */
    private Byte showDebug;

    /** 网关url, 数据库字段：gateway_url */
    private String gatewayUrl;

    /** 1：有效，0：无效, 数据库字段：status */
    private Byte status;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}