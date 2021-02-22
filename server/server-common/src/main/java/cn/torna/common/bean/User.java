package cn.torna.common.bean;

/**
 * 登录用户信息
 * @author tanghc
 */
public interface User {

    /**
     * 用户id
     * @return
     */
    Long getUserId();

    /**
     * 操作方式
     * @return
     */
    byte getOperationModel();

    /**
     * 是否超级管理员
     * @return
     */
    boolean isSuperAdmin();

    /**
     * 昵称
     * @return
     */
    String getNickname();

    Byte getStatus();

    String getToken();
}
