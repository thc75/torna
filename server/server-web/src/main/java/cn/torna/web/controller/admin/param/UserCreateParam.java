package cn.torna.web.controller.admin.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateParam {

    /** 登录账号/邮箱, 数据库字段：username */
    @NotBlank
    private String username;

    /** 昵称, 数据库字段：nickname */
    @NotBlank
    private String nickname;

    /** 密码, 数据库字段：password */
    @NotBlank
    private String  password;

    /** 邮箱, 数据库字段： email */
    private String email;

    @NotNull
    private Byte isSuperAdmin;

}