package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleCommonConfigParam {

    /**
     * 数据库字段：module_id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /**
     * 配置key, 数据库字段：config_key
     */
    private String configKey;

    /**
     * 配置值, 数据库字段：config_value
     */
    private String configValue;

}