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
public class DebugEnvDeleteParam {

    @ApiDocField(description = "环境名称", maxLength = "50", required = true, example = "测试环境")
    @NotBlank(message = "调试环境名称不能为空")
    private String name;

}
