package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class ComposeProjectDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 项目名称, 数据库字段：name */
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    /** 访问密码, 数据库字段：password */
    private String password;

    /**  数据库字段：creator_name */
    private String creatorName;

    /**  数据库字段：modifier_name */
    private String modifierName;

    /** 所属空间，space.id, 数据库字段：space_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    private String roleCode;

    private LocalDateTime gmtCreate;

    /** 访问形式，1：公开，2：加密, 数据库字段：type */
    private Byte type;

    /** 1：有效，0：无效, 数据库字段：status */
    private Byte status;

}
