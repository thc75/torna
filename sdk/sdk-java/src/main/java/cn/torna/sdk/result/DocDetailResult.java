package cn.torna.sdk.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocDetailResult {
    /** 文档id */
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

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 调试环境 */
    private List<DebugEnvResult> debugEnvs;

    /** 请求头 */
    private List<DocParamResult> headerParams;

    /** 请求参数 */
    private List<DocParamResult> requestParams;

    /** 返回参数 */
    private List<DocParamResult> responseParams;

    /** 错误码 */
    private List<DocParamResult> errorCodeParams;
}
