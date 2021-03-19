package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class MockDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 名称, 数据库字段：name */
    private String name;

    /** md5(path+query), 数据库字段：data_id */
    private String dataId;

    /** 请求参数, 数据库字段：query_string */
    private List<NameValueDTO> queryData;

    /** http状态, 数据库字段：http_status */
    private Integer httpStatus;

    /** 延迟时间，单位毫秒, 数据库字段：delay_mills */
    private Integer delayMills;

    /** 响应header，json结构, 数据库字段：response_headers */
    private List<NameValueDTO> responseHeaders;

    /** 响应结果, 数据库字段：response_body */
    private String responseBody;

    /** 文档id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 备注, 数据库字段：remark */
    private String remark;

    /** 创建人姓名, 数据库字段：creator_name */
    private String creatorName;

    /** 修改人, 数据库字段：modifier_name */
    private String modifierName;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;
}
