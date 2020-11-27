package torna.web.controller.module.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class DebugHostParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;
    private String debugHost;

}
