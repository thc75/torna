package torna.sdk.request;

import lombok.Data;
import torna.sdk.response.DocGetResponse;

/**
 * 获取文档详情, 接口名	doc.get	版本号	1.0
 *
 * @author tanghc
 */
@Data
public class DocGetRequest extends BaseRequest<DocGetResponse> {

    private String id;

    @Override
    public String name() {
        return "doc.get";
    }
}
