package cn.torna.sdk.request;

import cn.torna.sdk.response.DocPushResponse;
import lombok.Getter;
import lombok.Setter;
import cn.torna.sdk.param.DocItem;

import java.util.List;

/**
 * 推送文档
 * 接口名	doc.push	版本号	1.0
 * @author tanghc
 */
@Setter
@Getter
public class DocPushRequest extends BaseRequest<DocPushResponse> {

    /** baseUrl */
    private String baseUrl;

    /** 接口项 */
    private List<DocItem> apis;

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
