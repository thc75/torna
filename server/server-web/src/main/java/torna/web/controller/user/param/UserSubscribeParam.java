package torna.web.controller.user.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class UserSubscribeParam {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long sourceId;

}
