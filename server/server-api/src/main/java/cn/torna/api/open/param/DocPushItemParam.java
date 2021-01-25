package cn.torna.api.open.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author tanghc
 */
@Data
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

    /** 访问URL, 数据库字段：url */
    @ApiDocField(description = "url", example = "/goods/get")
    @Length(max = 128, message = "'url' 长度不能超过128")
    private String url;

    /** http方法, 数据库字段：http_method */
    @ApiDocField(description = "http方法", example = "GET")
    @Length(max = 12, message = "'httpMethod' 长度不能超过12")
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    @ApiDocField(description = "contentType", example = "application/json")
    @Length(max = 128, message = "'contentType' 长度不能超过128")
    private String contentType;

    @ApiDocField(description = "contentType", example = "1")
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点, 没有填空字符串", dataType = DataType.STRING)
    private Long parentId;

    /** 是否显示, 数据库字段：is_show */
    @ApiDocField(description = "是否显示，1：显示，0：不显示", example = "1")
    private Byte isShow;

    @ApiDocField(description = "请求头", elementClass = HeaderParamPushParam.class)
    private List<HeaderParamPushParam> headerParams;

    @ApiDocField(description = "请求参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> requestParams;

    @ApiDocField(description = "返回参数", elementClass = DocParamPushParam.class)
    private List<DocParamPushParam> responseParams;

    @ApiDocField(description = "错误码", elementClass = CodeParamPushParam.class)
    private List<CodeParamPushParam> errorCodeParams;

    @ApiDocField(description = "文档项")
    private List<DocPushItemParam> items;
}
