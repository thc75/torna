package cn.torna.web.controller.system.vo;

import lombok.Data;

@Data
public class MFABindVerifyVO {

    /**
     * 验证结果
     */
    private Boolean checkStatus;

    /**
     * LoginResult
     */
    private LoginResult loginResult;
}
