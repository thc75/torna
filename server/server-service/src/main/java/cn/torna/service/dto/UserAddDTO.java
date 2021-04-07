package cn.torna.service.dto;

import cn.torna.common.enums.UserInfoSourceEnum;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class UserAddDTO {
    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 登录密码 md5, 数据库字段：password */
    private String password;

    /** 昵称, 数据库字段：nickname */
    private String nickname;

    /** 是否是管理员, 数据库字段：is_admin */
    private Byte isSuperAdmin;

    private UserInfoSourceEnum sourceEnum;

    private Byte status;

}
