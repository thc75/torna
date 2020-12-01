package torna.sdk.request;

import torna.sdk.response.DocCategoryListResponse;

/**
 * 获取分类
 * 接口名	doc.category.list	版本号	1.0
 * @author tanghc
 */
public class DocCategoryListRequest extends BaseRequest<DocCategoryListResponse> {
    @Override
    public String name() {
        return "doc.category.list";
    }
}
