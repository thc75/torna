package torna.sdk.request;

import torna.sdk.response.DocCategoryNameUpdateResponse;

/**
 * 修改分类名称
 * 接口名	doc.category.name.update	版本号	1.0
 * @author tanghc
 */
public class DocCategoryNameUpdateRequest extends BaseRequest<DocCategoryNameUpdateResponse> {
    @Override
    public String name() {
        return "doc.category.name.update";
    }
}
