package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class DocFolderAddParam {
    @NotNull(message = "模块id不能为空")
    private Long moduleId;

    @NotBlank(message = "模块id不能为空")
    private String name;
}
