package torna.common.bean;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
@Slf4j
public class DefaultTokenManager implements TokenManager {


    @Value("${torna.token-timeout-seconds:600}")
    private int accessTokenTimeoutSeconds;

    private LoadingCache<String, Optional<User>> accessTokenCache;

    @Override
    public User getUser(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return accessTokenCache.getUnchecked(token).orElse(null);
    }

    @Override
    public void setUser(String token, User user) {
        accessTokenCache.put(token, Optional.of(user));
    }

    @Override
    public void removeUser(String token) {
        if (token == null) {
            return;
        }
        accessTokenCache.invalidate(token);
    }

    private static <T> LoadingCache<String, Optional<T>> buildCache(int timeout) {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        if (timeout > 0) {
            cacheBuilder.expireAfterAccess(timeout, TimeUnit.SECONDS);
        }
        return cacheBuilder
                .build(new CacheLoader<String, Optional<T>>() {
                    @Override
                    public Optional<T> load(String key) throws Exception {
                        return Optional.empty();
                    }
                });
    }

    @PostConstruct
    public void after() {
        accessTokenCache = buildCache(accessTokenTimeoutSeconds);
    }
}
