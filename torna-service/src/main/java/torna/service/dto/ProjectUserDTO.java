package torna.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class ProjectUserDTO {

    private Long id;

    private Long userId;

    /** 登录账号/邮箱, 数据库字段：username */
    private String username;

    /** 真实姓名, 数据库字段：realname */
    private String realname;

    private String roleCode;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
