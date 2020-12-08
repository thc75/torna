package torna.common.bean;

/**
 * @author tanghc
 */
public interface UserCacheManager {
    User getUser(long userId);

    void saveUser(User user);
}
