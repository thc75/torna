package cn.torna.web.controller.module.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DebugEnvSaveParam {

    @NotEmpty
    private List<DebugEnvParam> debugEnvs;

}
