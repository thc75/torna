package cn.torna.web.controller.admin.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class UserInfoParam {

    @NotNull(message = "id can not null")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;
}
