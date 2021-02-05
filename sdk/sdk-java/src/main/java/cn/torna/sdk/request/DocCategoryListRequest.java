package cn.torna.sdk.request;

import cn.torna.sdk.response.DocCategoryListResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取分类
 * 接口名	doc.category.list	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocCategoryListRequest extends BaseRequest<DocCategoryListResponse> {

    /**
     * @param token OpenAPI对应的token
     */
    public DocCategoryListRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.category.list";
    }
}
