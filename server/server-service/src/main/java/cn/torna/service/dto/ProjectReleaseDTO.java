package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 *
 * @author qiuyu
 */
@Data
public class ProjectReleaseDTO {

    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** project.id, 数据库字段：project_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 版本号 */
    private String releaseNo;

    /** 版本描述 */
    private String releaseDesc;

    /** 状态 1-有效 0-无效 */
    private Integer status;

    /** 钉钉机器人webhook */
    private String dingdingWebhook;

    /** 企业微信机器人webhook */
    private String weComWebhook;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

    /** 关联文档id */
    private Map<String, List<String>> moduleSourceIdMap;

    /** 当前用户是否关注此版本 */
    private Boolean isSubscribe = false;

}