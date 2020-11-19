package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class SpaceMemberAddParam {

    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @NotEmpty(message = "用户不能为空")
    private List<Long> userIds;

}
