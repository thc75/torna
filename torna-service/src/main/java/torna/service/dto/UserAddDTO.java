package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class UserAddDTO {
    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 登录密码, 数据库字段：password */
    private String password;

    /** 真实姓名, 数据库字段：realname */
    private String realname;

    /** 是否是管理员, 数据库字段：is_admin */
    private Byte isAdmin;
}
