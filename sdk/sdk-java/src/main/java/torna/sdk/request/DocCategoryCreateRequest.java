package torna.sdk.request;

import lombok.Data;
import torna.sdk.response.DocCategoryCreateResponse;

/**
 * 创建分类
 * 接口名	doc.category.create	版本号	1.0
 * @author tanghc
 */
@Data
public class DocCategoryCreateRequest extends BaseRequest<DocCategoryCreateResponse> {

    private String name;

    @Override
    public String name() {
        return "doc.category.create";
    }
}
