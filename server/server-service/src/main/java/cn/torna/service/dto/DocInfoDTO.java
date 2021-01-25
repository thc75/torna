package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import cn.torna.service.dataid.DocInfoDataId;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocInfoDTO implements DocInfoDataId {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 文档名称, 数据库字段：name */
    private String name;

    /** 文档概述, 数据库字段：description */
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

    /** 模块id，module.id, 数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 是否显示, 数据库字段：is_show */
    private Byte isShow;

    private Byte isDeleted;

    private String remark;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;

    private String baseUrl;

    private List<DocParamDTO> pathParams;
    private List<DocParamDTO> headerParams;
    private List<DocParamDTO> globalHeaderParams;
    private List<DocParamDTO> requestParams;
    private List<DocParamDTO> responseParams;
    private List<DocParamDTO> errorCodeParams;

}
