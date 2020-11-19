package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class SpaceUpdateParam {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "名称不能为空")
    private String name;

}
