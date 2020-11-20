package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectModuleDeleteParam {

    @NotNull(message = "模块id不能为空")
    private Long id;

}
