package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleConfigParam {

    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /**  数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 配置类型，1：全局header, 数据库字段：type */
    private Integer type;

    /** 配置key, 数据库字段：config_key */
    private String configKey;

    /** 配置值, 数据库字段：config_value */
    private String configValue;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long extendId;

    private String dataType;

    private String description;

}