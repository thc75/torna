package torna.api.open.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class DocInfoResult {
    @ApiDocField(description = "文档id")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

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

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    @ApiDocField(description = "是否是分类，0：不是，1：是")
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "父节点")
    private Long parentId;

    /** 是否显示, 数据库字段：is_show */
    @ApiDocField(description = "是否显示，1：显示，0：不显示")
    private Byte isShow;

}
