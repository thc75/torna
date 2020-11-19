package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class SpaceParam {

    @NotNull
    private Long id;
}
