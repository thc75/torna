package torna.web.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class UserInfoVO {
    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 真实姓名, 数据库字段：realname */
    private String realname;

    /** 是否是管理员, 数据库字段：is_admin */
    private Byte isAdmin;

    /** 中文拼音, 数据库字段：pinyin */
    private String pinyin;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;
}
