package cn.torna.sdk.request;

import cn.torna.sdk.response.DocCategoryNameUpdateResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改分类名称
 * 接口名	doc.category.name.update	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocCategoryNameUpdateRequest extends BaseRequest<DocCategoryNameUpdateResponse> {

    /** 文档id */
    private String id;

    /** 分类名称 */
    private String name;

    /**
     * @param token OpenAPI对应的token
     */
    public DocCategoryNameUpdateRequest(String token) {
        super(token);
    }


    @Override
    public String name() {
        return "doc.category.name.update";
    }
}
