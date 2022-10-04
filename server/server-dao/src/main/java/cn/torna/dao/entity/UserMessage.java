package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：user_message
 * 备注：站内消息
 *
 * @author tanghc
 */
@Table(name = "user_message", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class UserMessage {

    /**  数据库字段：id */
    private Long id;

    /** user_info.id, 数据库字段：user_id */
    private Long userId;

    /**  数据库字段：message */
    private String message;

    /**  数据库字段：is_read */
    private Byte isRead;

    /** 同user_subscribe.type, 数据库字段：type */
    private Byte type;

    /** 同user_subscribe.source_id, 数据库字段：source_id */
    private Long sourceId;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}