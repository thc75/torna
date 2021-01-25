package cn.torna.sdk.request;

import lombok.Getter;
import lombok.Setter;
import cn.torna.sdk.response.DocListResponse;

/**
 * 获取文档列表
 * 接口名	doc.list	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocListRequest extends BaseRequest<DocListResponse> {

    /**
     * @param token OpenAPI对应的token
     */
    public DocListRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.list";
    }
}
