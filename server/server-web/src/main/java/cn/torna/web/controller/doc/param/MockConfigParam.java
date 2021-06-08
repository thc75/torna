package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class MockConfigParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 名称, 数据库字段：name */
    private String name;

    private List<NameValueDTO> dataKv;

    private String dataJson;

    private String path;

    /** 参数类型，0：KV形式，1：json形式, 数据库字段：request_data_type */
    private Byte requestDataType;

    /** http状态, 数据库字段：http_status */
    private Integer httpStatus;

    /** 延迟时间，单位毫秒, 数据库字段：delay_mills */
    private Integer delayMills;

    /** 响应header，数组结构, 数据库字段：response_headers */
    private List<NameValueDTO> responseHeaders;

    private Byte resultType;

    /** 响应结果, 数据库字段：response_body */
    private String responseBody;

    private String mockScript;

    private String mockResult;

    /** 文档id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 备注, 数据库字段：remark */
    private String remark;

}
