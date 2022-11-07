package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 表名：mock_config
 * 备注：mock配置
 *
 * @author tanghc
 */
@Table(name = "mock_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class MockConfig {

    /**  数据库字段：id */
    private Long id;

    /** 名称, 数据库字段：name */
    private String name;

    /** md5(path+query+body), 数据库字段：data_id */
    private String dataId;

    /**  数据库字段：path */
    private String path;

    /** 过滤ip, 数据库字段：ip */
    private String ip;

    /** 请求参数, 数据库字段：request_data */
    private String requestData;

    /** 参数类型，0：KV形式，1：json形式, 数据库字段：request_data_type */
    private Byte requestDataType;

    /** http状态, 数据库字段：http_status */
    private Integer httpStatus;

    /** 延迟时间，单位毫秒, 数据库字段：delay_mills */
    private Integer delayMills;

    /** 返回类型，0：自定义内容，1：脚本内容, 数据库字段：result_type */
    private Byte resultType;

    /** 响应header，数组结构, 数据库字段：response_headers */
    private String responseHeaders;

    /** 响应结果, 数据库字段：response_body */
    private String responseBody;

    /** mock脚本, 数据库字段：mock_script */
    private String mockScript;

    /** mock结果, 数据库字段：mock_result */
    private String mockResult;

    /** 文档id, 数据库字段：doc_id */
    private Long docId;

    /** 备注, 数据库字段：remark */
    private String remark;

    /** 创建人id, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建人姓名, 数据库字段：creator_name */
    private String creatorName;

    /** 修改人id, 数据库字段：modifier_id */
    private Long modifierId;

    /** 修改人, 数据库字段：modifier_name */
    private String modifierName;

    /**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /**  数据库字段：gmt_modified */
    private LocalDateTime gmtModified;


}