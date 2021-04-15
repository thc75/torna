package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocPushParam {

    @ApiDocField(description = "调试环境", elementClass = DebugEnvParam.class)
    private List<DebugEnvParam> debugEnvs;

    @ApiDocField(description = "api列表", required = true, elementClass = DocPushItemParam.class)
    @NotEmpty(message = "文档内容不能为空")
    private List<DocPushItemParam> apis;

}
