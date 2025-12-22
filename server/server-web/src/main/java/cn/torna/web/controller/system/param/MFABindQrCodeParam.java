package cn.torna.web.controller.system.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MFABindQrCodeParam {

    /**
     * 用户名
     */
    @NotBlank(message = "登陆用户名不能为空！")
    private String username;


}
