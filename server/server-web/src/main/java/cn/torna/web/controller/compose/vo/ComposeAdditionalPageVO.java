package cn.torna.web.controller.compose.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p> 表名：compose_additional_page 备注：聚合文档附加页</p>
 *
 * @author tanghc
 */
@Data
public class ComposeAdditionalPageVO {

    /**  */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** compose_project.id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 1:启用，0：禁用 */
    private Byte status;

    /** 排序值 */
    private Integer orderIndex;

    private LocalDateTime gmtCreate;
}