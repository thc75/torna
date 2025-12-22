package cn.torna.web.controller.system.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 六如
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ThirdLoginForm extends LoginForm {

    /** 回调地址 */
    private String redirectUrl;
}
