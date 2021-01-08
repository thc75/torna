package torna.web.controller.space.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class SpaceMemberAddParam {

    @NotNull(message = "空间ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    @NotEmpty(message = "用户不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> userIds;

    @NotBlank(message = "角色不能为空")
    private String roleCode;

}
