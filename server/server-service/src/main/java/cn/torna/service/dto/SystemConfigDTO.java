package cn.torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class SystemConfigDTO {
    /**  数据库字段：config_key */
    private String configKey;

    /**  数据库字段：config_value */
    private String configValue;

    /**  数据库字段：remark */
    private String remark;
}
