package cn.torna.sdk.request;

import cn.torna.sdk.response.ModuleDebugEnvDeleteResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class ModuleDebugEnvDeleteRequest extends BaseRequest<ModuleDebugEnvDeleteResponse> {

    /** 环境名称 */
    private String name;

    public ModuleDebugEnvDeleteRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "module.debug.env.delete";
    }
}
