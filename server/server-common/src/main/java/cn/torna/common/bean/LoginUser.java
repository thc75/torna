package cn.torna.common.bean;

import cn.torna.common.enums.OperationMode;
import lombok.Data;

import java.time.LocalDateTime;

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
    
	/** 昵称, 数据库字段：nickname */
    private String nickname;

    /** 是否是管理员, 数据库字段：is_admin */
    private Byte isSuperAdmin;
    
	/**  数据库字段：is_deleted */
    private Byte isDeleted;
    
	/**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    private Byte status;

    private String token;

    @Override
    public byte getOperationModel() {
        return OperationMode.MANUAL.getType();
    }

    @Override
    public boolean isSuperAdmin() {
        return isSuperAdmin != null && isSuperAdmin == Booleans.TRUE;
    }

    @Override
    public Long getUserId() {
        return id;
    }

}