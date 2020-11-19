package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class SpaceMemberRemoveParam {

    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @NotNull(message = "用户不能为空")
    private Long userId;

}
