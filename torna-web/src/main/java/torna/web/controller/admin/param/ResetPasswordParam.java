package torna.web.controller.admin.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ResetPasswordParam {

    @NotNull(message = "id can not null")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;
}
