package cn.torna.common.bean;

/**
 * @author tanghc
 */
public interface UserCacheManager {
    /**
     * 返回用户信息
     * @param userId 用户id
     * @return 查不到返回null
     */
    User getUser(long userId);

    /**
     * 保存用户
     * @param user 用户
     */
    void saveUser(User user);
}
