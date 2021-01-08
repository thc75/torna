package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ProjectInfoDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 项目名称, 数据库字段：name */
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    /** 创建者userid, 数据库字段：creator_id */
    private Long creatorId;

    /** 创建人 */
    private String creatorName;

    /** 修改人 */
    private String modifierName;

    /** 所属空间，space.id, 数据库字段：space_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    /** 项目管理员 */
    private List<UserInfoDTO> admins;

    /** 是否私有项目，1：是，0：否, 数据库字段：is_private */
    private Byte isPrivate;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
