package cn.torna.web.controller.user.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class UpdateInfoParam {

    @NotBlank(message = "nickname can not be null")
    private String nickname;

    private String email;

}
