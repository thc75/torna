package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

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

    private Byte isCompose;

    private Date gmtCreate;
}
