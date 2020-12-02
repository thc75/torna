package torna.sdk.request;

import lombok.Getter;
import lombok.Setter;
import torna.sdk.response.DocCategoryListResponse;

/**
 * 获取分类
 * 接口名	doc.category.list	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocCategoryListRequest extends BaseRequest<DocCategoryListResponse> {
    public DocCategoryListRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.category.list";
    }
}
