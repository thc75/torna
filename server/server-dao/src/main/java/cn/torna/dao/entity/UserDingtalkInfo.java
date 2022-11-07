package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：user_dingtalk_info
 * 备注：钉钉开放平台用户
 *
 * @author tanghc
 */
@Table(name = "user_dingtalk_info", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class UserDingtalkInfo {

    /**  数据库字段：id */
    private Long id;

    /** 用户在钉钉上面的昵称, 数据库字段：nick */
    private String nick;

    /** 员工名称。, 数据库字段：name */
    private String name;

    /** 员工邮箱。, 数据库字段：email */
    private String email;

    /** 员工的userid。, 数据库字段：userid */
    private String userid;

    /** 用户在当前开放应用所属企业的唯一标识。, 数据库字段：unionid */
    private String unionid;

    /** 用户在当前开放应用内的唯一标识。, 数据库字段：openid */
    private String openid;

    /** user_info.id, 数据库字段：user_info_id */
    private Long userInfoId;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}