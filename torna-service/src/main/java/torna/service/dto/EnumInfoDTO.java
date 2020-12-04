package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class EnumInfoDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 枚举名称, 数据库字段：name */
    private String name;

    /** 枚举说明, 数据库字段：description */
    private String description;

    /** module.id, 数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private List<EnumItemDTO> items;

}
