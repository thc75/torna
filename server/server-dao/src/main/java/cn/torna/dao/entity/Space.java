package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：space
 * 备注：分组表
 *
 * @author tanghc
 */
@Table(name = "space", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class Space {

    /**  数据库字段：id */
    private Long id;

    /** 空间名称, 数据库字段：name */
    private String name;

    /** 创建者userid, 数据库字段：creator_id */
    private Long creatorId;

    /**  数据库字段：creator_name */
    private String creatorName;

    /** 创建者userid, 数据库字段：modifier_id */
    private Long modifierId;

    /**  数据库字段：modifier_name */
    private String modifierName;

    /** 是否组合空间, 数据库字段：is_compose */
    private Byte isCompose;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}