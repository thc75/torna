package torna.common.bean;

import lombok.Data;

import java.util.Date;

/**
 * 表名：user_info
 * 备注：用户表
 *
 * @author tanghc
 */
@Data
public class LoginUser implements User {

    private Long id;
    
	/** 登录账号/邮箱, 数据库字段：username */
    private String username;
    
	/** 真实姓名, 数据库字段：realname */
    private String realname;

    /** 是否是管理员, 数据库字段：is_admin */
    private Byte isAdmin;
    
	/**  数据库字段：is_deleted */
    private Byte isDeleted;
    
	/**  数据库字段：gmt_create */
    private Date gmtCreate;

    @Override
    public boolean isAdmin() {
        return isAdmin != null && isAdmin == Booleans.TRUE;
    }

    @Override
    public Long getUserId() {
        return id;
    }
}