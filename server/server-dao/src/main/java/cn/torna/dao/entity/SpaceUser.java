package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：space_user
 * 备注：分组用户关系表
 *
 * @author tanghc
 */
@Table(name = "space_user", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class SpaceUser {

    /**  数据库字段：id */
    private Long id;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** space.id, 数据库字段：space_id */
    private Long spaceId;

    /** 角色，guest：访客，dev：开发者，admin：空间管理员, 数据库字段：role_code */
    private String roleCode;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}