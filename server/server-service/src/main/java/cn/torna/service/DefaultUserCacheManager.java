package cn.torna.service;

import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.UserInfoMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DefaultUserCacheManager implements UserCacheManager, InitializingBean {

    @Autowired
    private UserInfoMapper userInfoMapper;

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
        if (user == null) {
            return;
        }
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
                        User user = getLoginUser(id);
                        return Optional.ofNullable(user);
                    }
                });
    }

    /**
     * 获取登陆用户
     * @param id
     * @return
     */
    private User getLoginUser(long id) {
        UserInfo userInfo = userInfoMapper.getById(id);
        if (userInfo == null) {
            log.warn("登录用户不存在，userId：{}", id);
            return null;
        }
        if (userInfo.getStatus() == UserStatusEnum.DISABLED.getStatus()) {
            log.warn("用户被禁用, userId:{}, username:{}, nickname:{}", userInfo.getId(), userInfo.getUsername(), userInfo.getNickname());
            return null;
        }
        return CopyUtil.copyBean(userInfo, LoginUser::new);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        userCache = buildCache(timeoutMinutes);
    }

}
