package cn.torna.api.open.result;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocInfoResult {
    @ApiDocField(description = "文档id", dataType = DataType.STRING, maxLength = "12",example = "9VXEyXvg")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 文档名称, 数据库字段：name */
    @ApiDocField(description = "文档名称", maxLength = "50", example = "获取商品信息")
    private String name;

    /** 文档概述, 数据库字段：description */
    @ApiDocField(description = "文档概述", maxLength = "200", example = "根据ID查询商品信息")
    private String description;

    /** 访问URL, 数据库字段：url */
    @ApiDocField(description = "url", maxLength = "100", example = "/goods/get")
    private String url;

    /** http方法, 数据库字段：http_method */
    @ApiDocField(description = "http方法", maxLength = "50", example = "GET")
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    @ApiDocField(description = "contentType", maxLength = "50", example = "application/json")
    private String contentType;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    @ApiDocField(description = "是否是分类，0：不是，1：是", example = "0")
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点", dataType = DataType.STRING, example = "Br2jqzG7")
    private Long parentId;

    /** 是否显示, 数据库字段：is_show */
    @ApiDocField(description = "是否显示，1：显示，0：不显示", example = "1")
    private Byte isShow;

}
