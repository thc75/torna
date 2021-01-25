package cn.torna.common.bean;

/**
 * @author tanghc
 */
public interface TokenManager {

    /**
     * 根据token获取登录用户
     * @param token token
     * @return 返回登录用户，没有返回null
     */
    User getUser(String token);

    /**
     * 返回token
     * @param token
     * @param user
     * @return
     */
    void setUser(String token, User user);

    /**
     * 删除用户
     * @param token token
     */
    void removeUser(String token);
}
