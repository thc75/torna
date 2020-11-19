package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ProjectMemberAddParam {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotEmpty(message = "成员不能为空")
    private List<Long> userIds;

    @NotBlank(message = "角色不能为空")
    private String roleCode;
}
