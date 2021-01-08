package torna.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import torna.common.bean.User;
import torna.common.bean.UserCacheManager;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
@Service
public class DefaultUserCacheManager implements UserCacheManager {

    @Autowired
    private UserInfoService userInfoService;

    @Value("${torna.user-cache-timeout-minutes:15}")
    private int timeoutMinutes;

    // key: userId
    private LoadingCache<Long, Optional<User>> userCache;

    @Override
    public User getUser(long userId) {
        return userCache.getUnchecked(userId).orElse(null);
    }

    @Override
    public void saveUser(User user) {
        Objects.requireNonNull(user, "用户不能为空");
        userCache.put(user.getUserId(), Optional.of(user));
    }

    private LoadingCache<Long, Optional<User>> buildCache(int timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be gt 0");
        }
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        cacheBuilder.expireAfterAccess(timeout, TimeUnit.MINUTES);
        return cacheBuilder
                .build(new CacheLoader<Long, Optional<User>>() {
                    @Override
                    public Optional<User> load(Long id) throws Exception {
                        User user = userInfoService.getLoginUser(id);
                        return Optional.ofNullable(user);
                    }
                });
    }

    @PostConstruct
    public void after() {
        userCache = buildCache(timeoutMinutes);
    }
}
