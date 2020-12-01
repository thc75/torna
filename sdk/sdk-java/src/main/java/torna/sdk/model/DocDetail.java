package torna.sdk.model;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocDetail {
    private String id;

    /** 文档名称 */
    private String name;

    /** 文档概述 */
    private String description;

    /** 访问URL */
    private String url;

    /** http方法 */
    private String httpMethod;

    /** contentType */
    private String contentType;

    /** 父节点 */
    private String parentId;

    /** 是否显示 */
    private Byte isShow;

    /** 请求头 */
    private List<DocParam> headerParams;

    /** 请求参数 */
    private List<DocParam> requestParams;

    /** 返回参数 */
    private List<DocParam> responseParams;

    /** 错误码 */
    private List<DocParam> errorCodeParams;
}
