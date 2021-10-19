package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Setter
@Getter
public class DebugHostDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 配置key*/
    private String configKey;

    /** 配置值 */
    private String configValue;

    private Long extendId;
}
