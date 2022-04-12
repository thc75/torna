package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocItem {

    /** 文档名称 */
    private String name;

    /** 文档概述 */
    private String description;

    /** 维护人 */
    private String author;

    /** 访问URL */
    private String url;

    /** 废弃信息 */
    private String deprecated;

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

    /** 排序 */
    private Integer orderIndex;

    // 1.8.1
    /** 是否请求数组 */
    private Byte isRequestArray;

    /** 是否返回数组 */
    private Byte isResponseArray;

    /** 请求数组时元素类型, object/number/string/boolean */
    private String requestArrayType;

    /** 返回数组时元素类型, object/number/string/boolean */
    private String responseArrayType;

    /** 接口项 */
    private List<DocItem> items;

    /** path参数 */
    private List<DocParamPath> pathParams;

    /** 请求头 */
    private List<DocParamHeader> headerParams;

    /** Query参数 */
    private List<DocParamReq> queryParams;

    /** Body参数 */
    private List<DocParamReq> requestParams;

    /** 返回参数 */
    private List<DocParamResp> responseParams;

    /** 错误码 */
    private List<DocParamCode> errorCodeParams;

    private DubboInfo dubboInfo;

}
