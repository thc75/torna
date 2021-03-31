package cn.torna.web.controller.system.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class RegParam {

    @NotBlank(message = "username can not be null")
    @Length(max = 128)
    private String username;

    @NotBlank(message = "password can not be null")
    @Length(max = 64)
    private String password;

    @Length(max = 64)
    private String nickname;

}
