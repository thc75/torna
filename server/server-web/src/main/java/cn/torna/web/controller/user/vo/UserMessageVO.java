package cn.torna.web.controller.user.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备注：站内消息
 *
 * @author tanghc
 */
@Data
public class UserMessageVO {

    /**
     * 数据库字段：id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /**
     * user_info.id, 数据库字段：user_id
     */
    private Long userId;

    /**
     * 数据库字段：message
     */
    private String message;

    /**
     * 数据库字段：is_read
     */
    private Byte isRead;

    /** 同user_subscribe.type, 数据库字段：type */
    private Byte type;

    /** 同user_subscribe.source_id, 数据库字段：source_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long sourceId;

    /**
     * 数据库字段：gmt_create
     */
    private LocalDateTime gmtCreate;


}