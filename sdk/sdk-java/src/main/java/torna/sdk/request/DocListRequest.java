package torna.sdk.request;

import torna.sdk.response.DocListResponse;

/**
 * 获取文档列表
 * 接口名	doc.list	版本号	1.0
 * @author tanghc
 */
public class DocListRequest extends BaseRequest<DocListResponse> {
    @Override
    public String name() {
        return "doc.list";
    }
}
