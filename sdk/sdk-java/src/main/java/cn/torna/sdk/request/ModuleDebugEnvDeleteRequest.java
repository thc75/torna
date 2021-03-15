package cn.torna.sdk.request;

import cn.torna.sdk.response.ModuleDebugEnvDeleteResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除调试环境
 * 接口名：module.debug.env.delete 版本号：1.0
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
