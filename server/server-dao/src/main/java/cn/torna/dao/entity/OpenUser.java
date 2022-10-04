package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：open_user
 * 备注：开放用户
 *
 * @author tanghc
 */
@Table(name = "open_user", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class OpenUser {

    /**  数据库字段：id */
    private Long id;

    /** appKey, 数据库字段：app_key */
    private String appKey;

    /** secret, 数据库字段：secret */
    private String secret;

    /** 1启用，0禁用, 数据库字段：status */
    private Byte status;

    /**  数据库字段：applicant */
    private String applicant;

    /** space.id, 数据库字段：space_id */
    private Long spaceId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}