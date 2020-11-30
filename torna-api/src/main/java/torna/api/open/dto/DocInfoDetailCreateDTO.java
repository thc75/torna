package torna.api.open.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocInfoDetailCreateDTO {

    /** 文档名称, 数据库字段：name */
    @ApiDocField(description = "文档名称")
    private String name;

    /** 文档概述, 数据库字段：description */
    @ApiDocField(description = "文档概述")
    private String description;

    /** 访问URL, 数据库字段：url */
    @ApiDocField(description = "url")
    private String url;

    /** http方法, 数据库字段：http_method */
    @ApiDocField(description = "http方法")
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    @ApiDocField(description = "contentType")
    private String contentType;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点")
    private Long parentId;

    /** 是否显示, 数据库字段：is_show */
    @ApiDocField(description = "是否显示，1：显示，0：不显示")
    private Byte isShow;

    @ApiDocField(description = "请求头", elementClass = DocParamDTO.class)
    private List<DocParamDTO> headerParams;

    @ApiDocField(description = "请求参数", elementClass = DocParamDTO.class)
    private List<DocParamDTO> requestParams;

    @ApiDocField(description = "返回参数", elementClass = DocParamDTO.class)
    private List<DocParamDTO> responseParams;

    @ApiDocField(description = "错误码", elementClass = DocParamDTO.class)
    private List<DocParamDTO> errorCodeParams;

}
