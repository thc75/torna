package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class UserInfoDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 昵称, 数据库字段：nickname */
    private String nickname;

    /** 注册来源，数据库字段：source */
    private String source;

    /** 邮箱，数据库字段：email */
    private String email;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    private Byte isSuperAdmin;

    private Byte status;

}
