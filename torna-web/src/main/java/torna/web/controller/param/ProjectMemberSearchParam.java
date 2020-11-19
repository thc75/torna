package torna.web.controller.param;

import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectMemberSearchParam extends PageParam {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    private String username;

    private String roleCode;
}
