package cn.torna.sdk.request;

import cn.torna.sdk.param.DebugEnv;
import cn.torna.sdk.param.DocItem;
import cn.torna.sdk.param.DocParamCode;
import cn.torna.sdk.param.EnumItemParam;
import cn.torna.sdk.response.DocPushResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 推送文档
 * 接口名	doc.push	版本号	1.0
 * @author tanghc
 */
@Setter
@Getter
public class DocPushRequest extends BaseRequest<DocPushResponse> {

    /** 调试环境 */
    private List<DebugEnv> debugEnvs;

    /** 接口项 */
    private List<DocItem> apis;

    /** 推送人 */
    private String author;

    /** 公共错误码 */
    private List<DocParamCode> commonErrorCodes;

    /**
     * @param token OpenAPI对应的token
     */
    public DocPushRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.push";
    }
}
