package cn.torna.service.dto;

import cn.torna.common.enums.UserInfoSourceEnum;
import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private UserInfoSourceEnum userInfoSourceEnum = UserInfoSourceEnum.REGISTER;
}
