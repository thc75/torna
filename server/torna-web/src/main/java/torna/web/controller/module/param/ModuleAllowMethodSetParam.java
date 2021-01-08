package torna.web.controller.module.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ModuleAllowMethodSetParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;
    private String method;

}
