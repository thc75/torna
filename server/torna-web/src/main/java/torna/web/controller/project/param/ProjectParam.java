package torna.web.controller.project.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectParam {
    @NotNull(message = "项目ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;
}
