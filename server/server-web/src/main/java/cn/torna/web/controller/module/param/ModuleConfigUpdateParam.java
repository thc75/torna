package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ModuleConfigUpdateParam {

    /**  数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private List<ModuleConfigItem> items;

    @Data
    public static class ModuleConfigItem {

        /** 配置类型，1：全局header, 数据库字段：type */
        private Byte type;

        /** 配置key, 数据库字段：config_key */
        private String configKey;

        /** 配置值, 数据库字段：config_value */
        private String configValue;

        private String description;
    }

}