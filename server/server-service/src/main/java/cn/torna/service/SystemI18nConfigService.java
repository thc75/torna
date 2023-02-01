package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.dao.mapper.SystemI18nConfigMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;


/**
 * @author tanghc
 */
@Service
public class SystemI18nConfigService extends BaseService<SystemI18nConfig, SystemI18nConfigMapper> {

    public JSONObject getContentByLang(String lang) {
        SystemI18nConfig systemI18nConfig = get("lang", lang);
        if (systemI18nConfig == null) {
            return null;
        }
        return JSON.parseObject(systemI18nConfig.getContent());
    }

}