package torna.sdk.request;

import lombok.Getter;
import lombok.Setter;
import torna.sdk.param.CodeParamCreateParam;
import torna.sdk.param.DocParamCreateParam;
import torna.sdk.param.HeaderParamCreateParam;
import torna.sdk.response.DocCreateResponse;

import java.util.List;

/**
 * 创建文档
 * 接口名	doc.create	版本号	1.0
 * @author tanghc
 */
@Getter
@Setter
public class DocCreateRequest extends BaseRequest<DocCreateResponse> {

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
    private List<HeaderParamCreateParam> headerParams;

    /** 请求参数 */
    private List<DocParamCreateParam> requestParams;

    /** 返回参数 */
    private List<DocParamCreateParam> responseParams;

    /** 错误码 */
    private List<CodeParamCreateParam> errorCodeParams;

    public DocCreateRequest(String token) {
        super(token);
    }

    @Override
    public String name() {
        return "doc.create";
    }
}
