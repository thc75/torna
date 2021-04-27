package cn.torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocItem {

    /** 文档名称 */
    private String name;

    /** 文档概述 */
    private String description;

    /** 访问URL */
    private String url;

    /** 方法定义 */
    private String definition;

    /** http方法 */
    private String httpMethod;

    /** contentType */
    private String contentType;

    /** 父节点 */
    private String parentId;

    /** 是否显示 */
    private Byte isShow;

    /** 是否分类 */
    private Byte isFolder;

    /** 接口项 */
    private List<DocItem> items;

    /** path参数 */
    private List<DocParamPath> pathParams;

    /** 请求头 */
    private List<DocParamHeader> headerParams;

    /** 请求参数 */
    private List<DocParamReq> requestParams;

    /** 返回参数 */
    private List<DocParamResp> responseParams;

    /** 错误码 */
    private List<DocParamCode> errorCodeParams;

    private DubboInfo dubboInfo;

}
