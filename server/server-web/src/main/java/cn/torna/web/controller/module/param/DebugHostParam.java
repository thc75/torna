package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class DebugHostParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @NotNull
    private Long moduleId;

    /** 配置key, 数据库字段：config_key */
    @NotBlank
    private String configKey;

    /** 配置值, 数据库字段：config_value */
    @NotBlank
    private String configValue;

}
