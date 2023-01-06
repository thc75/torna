package cn.torna.service.dto;

import cn.torna.common.annotation.Diff;
import cn.torna.common.enums.PositionType;
import cn.torna.common.support.IdCodec;
import cn.torna.service.dataid.DocInfoDataId;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocInfoDTO implements DocInfoDataId {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 文档名称, 数据库字段：name */
    @Diff(positionType = PositionType.DOC_NAME)
    private String name;

    /** 文档概述, 数据库字段：description */
    @Diff(positionType = PositionType.DOC_DESCRIPTION)
    private String description;

    /** 维护人, 数据库字段：author */
    @Diff(positionType = PositionType.AUTHOR)
    private String author;

    /** 0:http,1:dubbo, 数据库字段：protocol */
    private Byte type;

    /** 访问URL, 数据库字段：url */
    @Diff(positionType = PositionType.DOC_URL)
    private String url;

    /** http方法, 数据库字段：http_method */
    @Diff(positionType = PositionType.DOC_HTTP_METHOD)
    private String httpMethod;

    /** contentType, 数据库字段：content_type */
    @Diff(positionType = PositionType.CONTENT_TYPE)
    private String contentType;

    /** 废弃信息 */
    @Diff(positionType = PositionType.DEPRECATED)
    private String deprecated;

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder */
    private Byte isFolder;

    /** 父节点, 数据库字段：parent_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /** 模块id，module.id, 数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 项目id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 是否使用全局请求参数, 数据库字段：is_use_global_headers */
    @Diff(positionType = PositionType.USE_GLOBAL_HEADERS)
    private Byte isUseGlobalHeaders;

    /** 是否使用全局请求参数, 数据库字段：is_use_global_params */
    @Diff(positionType = PositionType.USE_GLOBAL_PARAMS)
    private Byte isUseGlobalParams;

    /** 是否使用全局返回参数, 数据库字段：is_use_global_returns */
    @Diff(positionType = PositionType.USE_GLOBAL_RETURNS)
    private Byte isUseGlobalReturns;

    /** 是否请求数组, 数据库字段：is_request_array */
    @Diff(positionType = PositionType.IS_REQUEST_ARRAY)
    private Byte isRequestArray;

    /** 是否返回数组, 数据库字段：is_response_array */
    @Diff(positionType = PositionType.IS_RESPONSE_ARRAY)
    private Byte isResponseArray;

    /** 请求数组时元素类型, 数据库字段：request_array_type */
    @Diff(positionType = PositionType.REQUEST_ARRAY_TYPE)
    private String requestArrayType;

    /** 返回数组时元素类型, 数据库字段：response_array_type */
    @Diff(positionType = PositionType.RESPONSE_ARRAY_TYPE)
    private String responseArrayType;

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 创建人, 数据库字段：creator_id */
    @JSONField(serialize = false)
    private Long creatorId;

    /** 修改人, 数据库字段：modifier_id */
    @JSONField(serialize = false)
    private Long modifierId;

    /** 是否显示, 数据库字段：is_show */
    @Diff(positionType = PositionType.IS_SHOW)
    private Byte isShow;

    @Diff(positionType = PositionType.ORDER_INDEX)
    private Byte isDeleted;

    private Byte isLocked;

    /** 文档状态 */
    @Diff(positionType = PositionType.STATUS)
    private Byte status;

    private String remark;

    private Integer orderIndex;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;

    private String baseUrl;

    private List<ModuleEnvironmentDTO> debugEnvs = Collections.emptyList();

    private Byte moduleType;

    @JSONField(serialize = false)
    private String md5;

    /** 空间id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    @Diff(positionType = PositionType.PATH_PARAM)
    private List<DocParamDTO> pathParams = Collections.emptyList();

    @Diff(positionType = PositionType.HEADER_PARAM)
    private List<DocParamDTO> headerParams = Collections.emptyList();

    @Diff(positionType = PositionType.QUERY_PARAM)
    private List<DocParamDTO> queryParams = Collections.emptyList();

    @Diff(positionType = PositionType.REQUEST_PARAM)
    private List<DocParamDTO> requestParams = Collections.emptyList();

    @Diff(positionType = PositionType.RESPONSE_PARAM)
    private List<DocParamDTO> responseParams = Collections.emptyList();
    private List<DocParamDTO> errorCodeParams = Collections.emptyList();

    private List<DocParamDTO> globalHeaders = Collections.emptyList();
    private List<DocParamDTO> globalParams = Collections.emptyList();
    private List<DocParamDTO> globalReturns = Collections.emptyList();

    private DubboInfoDTO dubboInfo;

    private String errorCodeInfo;
}
