package cn.torna.service;

import cn.torna.common.bean.EnvironmentContext;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.interfaces.IConfig;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemConfig;
import cn.torna.dao.mapper.SystemConfigMapper;
import cn.torna.service.dto.SystemConfigDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
@Service
public class SystemConfigService extends BaseLambdaService<SystemConfig, SystemConfigMapper> implements IConfig, InitializingBean {

    // key: configKey, value: configValue
    private final LoadingCache<String, Optional<String>> configCache = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Optional<String>>() {
                @Override
                public Optional<String> load(String key) throws Exception {
                    return Optional.ofNullable(getConfigValue(key, null));
                }
            });

    public void setConfig(String key, String value) {
        setConfig(key, value, "");
    }

    public void setConfig(String key, String value, String remark) {
        SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
        systemConfigDTO.setConfigKey(key);
        systemConfigDTO.setConfigValue(value);
        systemConfigDTO.setRemark(remark);
        setConfig(systemConfigDTO);
    }

    public void setConfig(SystemConfigDTO systemConfigDTO) {
        Objects.requireNonNull(systemConfigDTO.getConfigKey(), "need key");
        Objects.requireNonNull(systemConfigDTO.getConfigValue(), "need value");
        SystemConfig systemConfig = get(SystemConfig::getConfigKey, systemConfigDTO.getConfigKey());
        if (systemConfig == null) {
            systemConfig = CopyUtil.copyBean(systemConfigDTO, SystemConfig::new);
            this.save(systemConfig);
        } else {
            CopyUtil.copyPropertiesIgnoreNull(systemConfigDTO, systemConfig);
            this.update(systemConfig);
        }
        configCache.invalidate(systemConfigDTO.getConfigKey());
    }

    /**
     * 获取配置信息
     * <pre>
     *  优先级：
     *  数据库
     *  Environment
     *  默认配置
     * </pre>
     *
     * @param key          配置key
     * @param defaultValue 没有获取到返回的默认值
     * @return 返回配置信息，如果没有获取到值，则返回默认值
     */
    public String getConfigValue(String key, String defaultValue) {
        Objects.requireNonNull(key, "need key");
        SystemConfig systemConfig = get(SystemConfig::getConfigKey, key);
        return Optional.ofNullable(systemConfig)
                .map(SystemConfig::getConfigValue)
                .orElseGet(() -> {
                    String value = EnvironmentContext.getValue(key, defaultValue);
                    if (value == null) {
                        EnvironmentKeys environmentKeys = EnvironmentKeys.of(key);
                        if (environmentKeys != null && environmentKeys.getDefaultValue() != null) {
                            value = environmentKeys.getDefaultValue();
                        }
                    }
                    return value;
                });
    }

    @Override
    public String getConfig(String key) {
        return configCache.getUnchecked(key).orElse(null);
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        return configCache.getUnchecked(key).orElse(defaultValue);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.list(new Query()).forEach(systemConfig -> {
            configCache.put(systemConfig.getConfigKey(), Optional.of(systemConfig.getConfigValue()));
        });
    }
}
