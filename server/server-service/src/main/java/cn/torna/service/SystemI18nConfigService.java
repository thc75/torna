package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.dao.mapper.SystemI18nConfigMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @author tanghc
 */
@Service
public class SystemI18nConfigService extends BaseService<SystemI18nConfig, SystemI18nConfigMapper> {

    private final LoadingCache<String, Optional<JSONObject>> configCache = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Optional<JSONObject>>() {
                @Override
                public Optional<JSONObject> load(String key) throws Exception {
                    return Optional.ofNullable(doGetContentByLang(key));
                }
            });

    public JSONObject getContentByLang(String lang) {
        if (StringUtils.isEmpty(lang)) {
            return null;
        }
        return configCache.getUnchecked(lang).orElse(null);
    }

    private JSONObject doGetContentByLang(String lang) {
        SystemI18nConfig systemI18nConfig = getByLang(lang);
        if (systemI18nConfig == null) {
            return null;
        }
        return JSON.parseObject(systemI18nConfig.getContent());
    }

    public SystemI18nConfig getByLang(String lang) {
        return get("lang", lang);
    }

    @Override
    public int save(SystemI18nConfig entity) {
        int i = super.save(entity);
        refreshCache(entity.getLang());
        return i;
    }

    @Override
    public int saveOrUpdate(SystemI18nConfig entity) {
        int i = super.saveOrUpdateIgnoreNull(entity);
        refreshCache(entity.getLang());
        return i;
    }

    private void refreshCache(String lang) {
        configCache.refresh(lang);
    }
}