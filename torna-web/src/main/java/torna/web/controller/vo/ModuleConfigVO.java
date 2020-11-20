package torna.web.controller.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleConfigVO {

    /**  数据库字段：id */
    private Long id;

    /**  数据库字段：module_id */
    private Long moduleId;

    /** 配置类型，1：全局header, 数据库字段：type */
    private Byte type;

    /** 配置key, 数据库字段：config_key */
    private String configKey;

    /** 配置值, 数据库字段：config_value */
    private String configValue;

}