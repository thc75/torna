package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Table(name = "system_login_token")
@Data
public class SystemLoginToken {

    private Long id;

    private String loginKey;

    private String token;

    private LocalDateTime expireTime;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;

}
