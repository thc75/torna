package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class EnumItemDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** enum_info.id, 数据库字段：enum_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long enumId;

    /** 名称，字面值, 数据库字段：name */
    private String name;

    /** 类型, 数据库字段：type */
    private String type;

    /** 枚举值, 数据库字段：value */
    private String value;

    /** 枚举描述, 数据库字段：description */
    private String description;
}
