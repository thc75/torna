package cn.torna.sdk.request;

import cn.torna.sdk.response.DocCategoryCreateResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建分类
 * 接口名	doc.category.create	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocCategoryCreateRequest extends BaseRequest<DocCategoryCreateResponse> {

    /** 分类名称 */
    private String name;

    /**
     * @param token OpenAPI对应的token
     */
    public DocCategoryCreateRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.category.create";
    }
}
