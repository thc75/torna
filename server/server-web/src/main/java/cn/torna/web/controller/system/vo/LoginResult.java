package cn.torna.web.controller.system.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class LoginResult {
    private String token;
    private Byte status;
    private Boolean mfaEnable;
    /** 此用户是否为首次多因素认证 **/
    private Boolean isFirstMfaAuth;
}
