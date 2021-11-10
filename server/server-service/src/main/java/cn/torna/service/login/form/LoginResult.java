package cn.torna.service.login.form;

import cn.torna.common.enums.UserInfoSourceEnum;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class LoginResult {

    private String username;

    private String nickname;

    private String email;

    private UserInfoSourceEnum userInfoSourceEnum = UserInfoSourceEnum.FORM;

}
