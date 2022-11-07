package cn.torna.sdk.request;

import cn.torna.sdk.common.Booleans;
import cn.torna.sdk.param.DebugEnv;
import cn.torna.sdk.param.DocItem;
import cn.torna.sdk.param.DocParamCode;
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

    /** 是否替换文档，1：替换，0：不替换（追加）。默认：1 */
    private Byte isReplace = Booleans.TRUE;

    /** 是否覆盖文档，1：覆盖，0：不覆盖。缺省：0。如果只想修改部分接口传1 */
    private Byte isOverride = Booleans.FALSE;

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
