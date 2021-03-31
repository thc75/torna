package cn.torna.manager.captcha;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author tanghc
 */
@Service
public class CaptchaManager {

    @Autowired
    private CaptchaService captchaService;

    @Value("${torna.captcha.enable:false}")
    private String enableCaptcha;

    public void check(Object args) {
        if ("false".equalsIgnoreCase(enableCaptcha)) {
            return;
        }
        Assert.isTrue(args instanceof CaptchaAware, "缺少验证码参数CaptchaAware");
        CaptchaAware captchaAware = (CaptchaAware)args;
        CaptchaVO captcha = captchaAware.getCaptcha();
        Assert.notNull(captcha, "请校验验证码");
        ResponseModel responseModel = captchaService.verification(captcha);
        //验证码校验失败，返回信息告诉前端
        //repCode  0000  无异常，代表成功
        //repCode  9999  服务器内部异常
        //repCode  0011  参数不能为空
        //repCode  6110  验证码已失效，请重新获取
        //repCode  6111  验证失败
        //repCode  6112  获取验证码失败,请联系管理员
        if (!responseModel.isSuccess()) {
            throw new RuntimeException(responseModel.getRepMsg());
        }
    }


}
