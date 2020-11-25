package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class UserSpaceDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 分组名称, 数据库字段：name */
    private String name;

    /** 创建者userid, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /** 能否操作 */
    private Byte canOperate;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;
}
