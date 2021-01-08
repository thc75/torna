package torna.web.controller.module.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ModuleUpdateNameParam {

    @NotNull(message = "模块id不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    @NotBlank
    private String name;

}
