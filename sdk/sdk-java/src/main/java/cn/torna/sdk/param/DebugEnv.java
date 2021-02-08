package cn.torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugEnv {

    /** 环境名称 */
    private String name;

    /** 调试路径 */
    private String url;

    public DebugEnv(String name, String url) {
        this.name = name;
        this.url = url;
    }
}