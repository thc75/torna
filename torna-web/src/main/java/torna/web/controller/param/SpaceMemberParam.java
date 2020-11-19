package torna.web.controller.param;

import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class SpaceMemberParam extends PageParam {

    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    private String username;
}
