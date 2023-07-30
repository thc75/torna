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

    @ApiDocField(description = "推送人", example = "张三")
    private String author;

    @ApiDocField(description = "公共错误码", elementClass = CodeParamPushParam.class)
    private List<CodeParamPushParam> commonErrorCodes;

    @ApiDocField(description = "是否替换文档，1：替换，0：不替换（追加）。缺省：1", example = "1")
    private Byte isReplace;

}
