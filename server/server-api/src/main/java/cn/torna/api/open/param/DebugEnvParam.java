package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DebugEnvParam {

    @ApiDocField(description = "环境名称", maxLength = "50", required = true, example = "测试环境")
    @NotBlank(message = "调试环境名称不能为空")
    private String name;

    @ApiDocField(description = "调试路径",  maxLength = "100", required = true, example = "http://10.1.30.165:2222")
    @NotBlank(message = "调试路径不能为空")
    private String url;
}
