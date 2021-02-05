package cn.torna.api.open.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Setter
@Getter
public class DebugEnvResult {

    /** 配置key*/
    @ApiDocField(description = "环境名称", example = "测试环境")
    private String configKey;

    /** 配置值 */
    @ApiDocField(description = "调试路径", example = "http://10.0.1.31:8080")
    private String configValue;

}
