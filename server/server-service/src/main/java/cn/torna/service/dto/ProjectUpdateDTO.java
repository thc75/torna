package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ProjectUpdateDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 项目名称, 数据库字段：name */
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    private Long modifierId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    private String modifierName;

    /** 是否私有项目，1：是，0：否, 数据库字段：is_private */
    private Byte isPrivate;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> adminIds;

}
