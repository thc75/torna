package cn.torna.web.controller.system.param;

import cn.torna.common.enums.UserInfoSourceEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginForm {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String source = UserInfoSourceEnum.REGISTER.getSource();

}