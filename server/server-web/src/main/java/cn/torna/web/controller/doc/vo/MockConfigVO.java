package cn.torna.web.controller.doc.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tanghc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MockConfigVO extends MockBaseVO {
    private List<NameValueVO> dataKv;
    private String dataJson;

    /**  数据库字段：path */
    private String path;

    /** 参数类型，0：KV形式，1：json形式, 数据库字段：request_data_type */
    private Byte requestDataType;

    /** http状态, 数据库字段：http_status */
    private Integer httpStatus;

    /** 延迟时间，单位毫秒, 数据库字段：delay_mills */
    private Integer delayMills;

    /** 响应header，数组结构, 数据库字段：response_headers */
    private List<NameValueVO> responseHeaders;

    private Byte resultType;

    /** 响应结果, 数据库字段：response_body */
    private String responseBody;

    private String mockScript;

    private String mockResult;

    private Byte responseBodyType;

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
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;
}
