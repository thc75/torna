package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;


/**
 *
 * @author qiuyu
 */
@Data
public class ProjectReleaseDocDTO {

    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** project.id, 数据库字段：project_id */
    private Long projectId;

    /** project_release.id */
    private Long releaseId;

    /** doc_info.id */
    private Long sourceId;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

}