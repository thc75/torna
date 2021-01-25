package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.support.IdCodec;
import cn.torna.service.dataid.DocInfoDataId;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocItemCreateDTO implements DocInfoDataId {
    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档描述, 数据库字段：description */
    private String description;

    /** 访问URL, 数据库字段：url */
    private String url;

    /** http方法, 数据库字段：http_method */
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    private String contentType;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private User user;

}
