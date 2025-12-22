package cn.torna.web.controller.system;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.MultiFactorAuthenticatorUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.service.UserInfoService;
import cn.torna.web.controller.system.param.MFABindQrCodeParam;
import cn.torna.web.controller.system.param.MFABindVerifyParam;
import cn.torna.web.controller.system.vo.LoginResult;
import cn.torna.web.controller.system.vo.MFABindQrCodeVO;
import cn.torna.web.controller.system.vo.MFABindVerifyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author qiuyu
 */
@Slf4j
@RestController
@RequestMapping("mfa")
public class MultiFactorAuthController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取二维码
     */
    @NoLogin
    @PostMapping("/getQRCode")
    public Result<MFABindQrCodeVO> getQRCode(@Valid @RequestBody MFABindQrCodeParam mfaBindQrCodeParam) {
        UserInfo userInfo = userInfoService.getByUsername(mfaBindQrCodeParam.getUsername());
        if (userInfo == null) {
            throw new BizException("MFA验证异常，请联系管理员");
        }

        MFABindQrCodeVO mfaBindQrCodeVO = new MFABindQrCodeVO();
        String qrCode;
        if (StringUtils.isNotBlank(userInfo.getMfaSk())) {
            qrCode = MultiFactorAuthenticatorUtil.getQRBarcode(userInfo.getUsername(), userInfo.getMfaSk());
        } else {
            String secret = MultiFactorAuthenticatorUtil.generateSecretKey();
            qrCode = MultiFactorAuthenticatorUtil.getQRBarcode(userInfo.getUsername(), secret);
            userInfoService.updateMfaSk(userInfo.getId(), secret);
        }
        mfaBindQrCodeVO.setQrCode(qrCode);
        return Result.ok(mfaBindQrCodeVO);
    }

    /**
     * 两步验证
     */
    @NoLogin
    @PostMapping("/verify")
    public Result<MFABindVerifyVO> verify(@Valid @RequestBody MFABindVerifyParam param) {
        UserInfo userInfo = userInfoService.getByUsername(param.getUsername());
        if (userInfo == null) {
            throw new BizException("MFA验证异常，请联系管理员");
        }
        MFABindVerifyVO mfaBindVerifyVO = new MFABindVerifyVO();
        boolean checkStatus = MultiFactorAuthenticatorUtil.checkCode(userInfo.getMfaSk(), param.getTotpCode(), System.currentTimeMillis());
        mfaBindVerifyVO.setCheckStatus(checkStatus);
        if (checkStatus) {
            LoginUser loginUser = userInfoService.buildLoginUserAndSaveCache(userInfo);

            LoginResult loginResult = new LoginResult();
            loginResult.setToken(loginUser.getToken());
            loginResult.setStatus(loginUser.getStatus());
            loginResult.setMfaEnable(Boolean.parseBoolean(EnvironmentKeys.TORNA_MFA_ENABLE.getValue()));
            loginResult.setIsFirstMfaAuth(loginUser.getIsFirstMfaAuth());

            mfaBindVerifyVO.setLoginResult(loginResult);
        }
        return Result.ok(mfaBindVerifyVO);
    }

}
