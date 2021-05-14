package cn.torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DingTalkLoginDTO {
    private String nick;
    private String unionid;
    private String openid;
    private String userid;
    private String name;
    private String email;
    private Boolean boss;
}
