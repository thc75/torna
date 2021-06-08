package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class DebugEnvParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @NotNull
    private Long id;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 配置key, 数据库字段：config_key */
    @NotBlank
    @Length(max = 50)
    private String configKey;

    /** 配置值, 数据库字段：config_value */
    @NotBlank
    @Length(max = 100)
    private String configValue;

    private Long extendId;

}
