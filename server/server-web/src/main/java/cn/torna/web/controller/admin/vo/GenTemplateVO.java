package cn.torna.web.controller.admin.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备注：代码生成模板
 *
 * @author tanghc
 */
@Data
public class GenTemplateVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板内容
     */
    private String content;

    private Integer isDeleted;

    /**
     * 分组
     */
    private String groupName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
