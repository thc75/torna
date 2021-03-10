package cn.torna.sdk.request;

import cn.torna.sdk.response.ModuleDebugEnvSetResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 设置调试环境
 * 接口名：module.debug.env.set 版本号：1.0
 * @author tanghc
 */
@Getter
@Setter
public class ModuleDebugEnvSetRequest extends BaseRequest<ModuleDebugEnvSetResponse> {

    /** 环境名称 */
    private String name;

    /** 调试路径 */
    private String url;

    public ModuleDebugEnvSetRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "module.debug.env.set";
    }
}
