package torna.sdk.request;

import lombok.Getter;
import lombok.Setter;
import torna.sdk.response.DocCategoryNameUpdateResponse;

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

    public DocCategoryNameUpdateRequest(String token) {
        super(token);
    }


    @Override
    public String name() {
        return "doc.category.name.update";
    }
}
