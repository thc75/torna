package cn.torna.sdk.request;

import cn.torna.sdk.response.DocGetResponse;
import cn.torna.sdk.result.DocDetailResult;
import cn.torna.sdk.result.DocParamResult;
import cn.torna.sdk.util.TreeUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 获取文档详情, 接口名	doc.get	版本号	1.0
 *
 * @author tanghc
 */
@Getter
@Setter
public class DocGetRequest extends BaseRequest<DocGetResponse> {

    /** 文档id */
    private String id;

    /**
     * @param token OpenAPI对应的token
     */
    public DocGetRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.get";
    }

    @Override
    public DocGetResponse parseResponse(String resp) {
        DocGetResponse docGetResponse = super.parseResponse(resp);
        DocDetailResult data = docGetResponse.getData();
        List<DocParamResult> reqParams = TreeUtil.convertTree(data.getRequestParams(), "");
        List<DocParamResult> respParams = TreeUtil.convertTree(data.getResponseParams(), "");
        data.setRequestParams(reqParams);
        data.setResponseParams(respParams);
        return docGetResponse;
    }
}
