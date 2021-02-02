package cn.torna.manager.captcha;

import com.anji.captcha.model.vo.CaptchaVO;

/**
 * @author tanghc
 */
public interface CaptchaAware {
    /**
     * 获取验证码参数
     *
     * @return
     */
    CaptchaVO getCaptcha();
}
