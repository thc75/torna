package torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocItem {

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

    /** 是否分类 */
    private Byte isFolder;

    /** 接口项 */
    private List<DocItem> items;

    /** 请求头 */
    private List<DocParamHeader> headerParams;

    /** 请求参数 */
    private List<DocParamReq> requestParams;

    /** 返回参数 */
    private List<DocParamResp> responseParams;

    /** 错误码 */
    private List<DocParamCode> errorCodeParams;

}
