package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemConfig;
import cn.torna.dao.mapper.SystemConfigMapper;
import cn.torna.service.dto.SystemConfigDTO;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @author tanghc
 */
@Service
public class SystemConfigService extends BaseService<SystemConfig, SystemConfigMapper> {

    public void setConfig(String key, String value) {
        SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
        systemConfigDTO.setConfigKey(key);
        systemConfigDTO.setConfigValue(value);
        setConfig(systemConfigDTO);
    }

    public void setConfig(SystemConfigDTO systemConfigDTO) {
        Objects.requireNonNull(systemConfigDTO.getConfigKey(), "need key");
        Objects.requireNonNull(systemConfigDTO.getConfigValue(), "need value");
        SystemConfig systemConfig = get("config_key", systemConfigDTO.getConfigKey());
        if (systemConfig == null) {
            systemConfig = CopyUtil.copyBean(systemConfigDTO, SystemConfig::new);
            this.save(systemConfig);
        } else {
            CopyUtil.copyPropertiesIgnoreNull(systemConfigDTO, systemConfig);
            this.update(systemConfig);
        }
    }

    public String getConfigValue(String key, String defaultValue) {
        Objects.requireNonNull(key, "need key");
        SystemConfig systemConfig = get("config_key", key);
        return Optional.ofNullable(systemConfig)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

}