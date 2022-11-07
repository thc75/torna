package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：user_subscribe
 * 备注：用户订阅表
 *
 * @author tanghc
 */
@Table(name = "user_subscribe", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class UserSubscribe {

    /**  数据库字段：id */
    private Long id;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /** 类型，1：订阅接口，2：订阅项目, 数据库字段：type */
    private Byte type;

    /** 关联id，当type=1对应doc_info.id；type=2对应project.id, 数据库字段：source_id */
    private Long sourceId;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}