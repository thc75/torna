package cn.torna.web.controller.system.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class LoginResult {
    private String token;
    private Byte status;
}
