package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectParam {
    @NotNull(message = "项目ID不能为空")
    private Long id;
}
