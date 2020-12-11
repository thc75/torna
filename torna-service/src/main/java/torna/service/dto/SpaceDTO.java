package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class SpaceDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 空间名称, 数据库字段：name */
    private String name;

    private String creatorName;

    /** 是否是个人默认空间，1：是，0：否, 数据库字段：is_default */
    private Byte isDefault;

    private Date gmtCreate;
}
