package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectMemberUpdateParam {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "角色不能为空")
    private String roleCode;
}
