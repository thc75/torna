package cn.torna.sdk.result;

import lombok.Getter;
import lombok.Setter;

/**
 * 调试环境配置项
 */
@Setter
@Getter
public class DebugEnvResult {

    /** 环境名称*/
    private String configKey;

    /** 调试路径 */
    private String configValue;

}