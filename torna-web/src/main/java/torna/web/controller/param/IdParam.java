package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class IdParam {
    @NotNull(message = "id不能为空")
    private Long id;
}
