package cn.torna.web.controller.system.param;

import cn.torna.manager.captcha.CaptchaAware;
import com.anji.captcha.model.vo.CaptchaVO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class RegParam implements CaptchaAware {

    @NotBlank(message = "username can not be null")
    @Length(max = 128)
    private String username;

    @NotBlank(message = "password can not be null")
    @Length(max = 64)
    private String password;

    @Length(max = 64)
    private String nickname;

    private CaptchaVO captcha;
}
