package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

/**
 * @author tanghc
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DocPushItemParam {

    /** 文档名称, 数据库字段：name */
    @ApiDocField(description = "文档名称", required = true, example = "获取商品信息")
    @NotBlank(message = "文档名称不能为空")
    @Length(max = 64, message = "文档名称长度不能超过64")
    private String name;

    /** 文档概述, 数据库字段：description */
    @ApiDocField(description = "文档概述", example = "获取商品信息")
    @Length(max = 128, message = "'description' 长度不能超过128")
    private String description;

    @ApiDocField(description = "接口维护人", example = "李四")
    private String author;

    @ApiDocField(description = "废弃描述，没有不传，传了值就代表废弃", example = "该接口已废弃，改成:/user/list")
    @Length(max = 128, message = "'deprecated' 长度不能超过128")
    private String deprecated;

    /** 0:http,1:dubbo */
    private Byte type;

    /** 访问URL, 数据库字段：url */
    @ApiDocField(description = "请求url", example = "/goods/get")
    @Length(max = 200, message = "'url' 长度不能超过200")
    private String url;

    @ApiDocField(description = "dubbo的接口方法定义", example = "Result<Order> getOrder(String orderNo)")
    @Length(max = 200, message = "'definition' 长度不能超过200")
    private String definition;

    /** http方法, 数据库字段：http_method */
    @ApiDocField(description = "http方法", example = "GET")
    @Length(max = 12, message = "'httpMethod' 长度不能超过12")
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    @ApiDocField(description = "contentType", example = "application/json")
    @Length(max = 128, message = "'contentType' 长度不能超过128")
    private String contentType;

    @ApiDocField(description = "是否是文件夹，1：是，0：否", example = "1")
    private Byte isFolder;

    /** 是否显示, 数据库字段：is_show */
    @ApiDocField(description = "是否显示，1：显示，0：不显示", example = "1")
    private Byte isShow;

    /** 排序 */
    @ApiDocField(description = "排序, 值小靠前", example = "0")
    private Integer orderIndex;

    // 1.8.1
    @ApiDocField(description = "是否请求数组", example = "0")
    private Byte isRequestArray = 0;

    @ApiDocField(description = "是否返回数组", example = "0")
    private Byte isResponseArray = 0;

    @ApiDocField(description = "请求数组时元素类型, object/number/string/boolean", example = "object")
    @Builder.Default
    private String requestArrayType = "object";

    @ApiDocField(description = "返回数组时元素类型, object/number/string/boolean", example = "object")
    @Builder.Default
    private String responseArrayType = "object";

    // dubbo
    @ApiDocField(description = "dubbo服务信息")
    private DubboParam dubboInfo;

    @ApiDocField(description = "path参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> pathParams;

    @ApiDocField(description = "请求头", elementClass = HeaderParamPushParam.class)
    private List<HeaderParamPushParam> headerParams;

    @ApiDocField(description = "Query参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> queryParams;

    @ApiDocField(description = "Body参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> requestParams;

    @ApiDocField(description = "返回参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> responseParams;

    @ApiDocField(description = "错误码", elementClass = CodeParamPushParam.class)
    private List<CodeParamPushParam> errorCodeParams;

    @ApiDocField(description = "文档项")
    private List<DocPushItemParam> items;

    private String tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocPushItemParam that = (DocPushItemParam) o;
        return name.equals(that.name) &&
                Objects.equals(url, that.url) &&
                Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(isFolder, that.isFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, httpMethod, isFolder);
    }

}
