package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：user_message
 * 备注：站内消息
 *
 * @author tanghc
 */
@Table(name = "user_message")
@Data
public class UserMessage {

    /**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;


}