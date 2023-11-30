package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表名：user_wecom_info
 * 备注：企业微信开放平台用户
 *
 * @author Lin
 * @date 2023-11-29  17:02:14
 */
@Table(name = "user_wecom_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class UserWeComInfo {

    /**  数据库字段：id */
    private Long id;

    /**  企业微信绑定手机号码：mobile */
    private String mobile;

    /** user_info.id, 数据库字段：user_info_id */
    private Long userInfoId;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

        /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;
}
