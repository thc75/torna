package torna.sdk.request;

import lombok.Getter;
import lombok.Setter;
import torna.sdk.param.CodeParamCreateParam;
import torna.sdk.param.CodeParamUpdateParam;
import torna.sdk.param.DocParamCreateParam;
import torna.sdk.param.DocParamUpdateParam;
import torna.sdk.param.HeaderParamCreateParam;
import torna.sdk.param.HeaderParamUpdateParam;
import torna.sdk.response.DocCreateResponse;
import torna.sdk.response.DocUpdateResponse;

import java.util.List;

/**
 * 修改文档
 * 接口名	doc.update	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocUpdateRequest extends BaseRequest<DocUpdateResponse> {

    /** 文档id */
    private String id;

    /** 文档名称 name */
    private String name;

    /** 文档概述 description */
    private String description;

    /** 访问URL url */
    private String url;

    /** http方法 http_method */
    private String httpMethod;

    /** contentType content_type */
    private String contentType;

    /** 父节点 parent_id */
    private String parentId;

    /** 是否显示 is_show */
    private Byte isShow;

    /** 请求头 */
    private List<HeaderParamUpdateParam> headerParams;

    /** 请求参数 */
    private List<DocParamUpdateParam> requestParams;

    /** 返回参数 */
    private List<DocParamUpdateParam> responseParams;

    /** 错误码 */
    private List<CodeParamUpdateParam> errorCodeParams;

    public DocUpdateRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.update";
    }
}
