package torna.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class UserInfoDTO {

    private Long id;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 真实姓名, 数据库字段：realname */
    private String realname;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
