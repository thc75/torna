package cn.torna.sdk.request;

import cn.torna.sdk.response.DocGetResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取文档详情, 接口名	doc.get	版本号	1.0
 *
 * @author tanghc
 */
@Getter
@Setter
public class DocGetRequest extends BaseRequest<DocGetResponse> {

    /** 文档id */
    private String id;

    /**
     * @param token OpenAPI对应的token
     */
    public DocGetRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.get";
    }
}
