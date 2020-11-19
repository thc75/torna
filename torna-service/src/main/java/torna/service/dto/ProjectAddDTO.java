package torna.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ProjectAddDTO {
    /** 项目名称, 数据库字段：name */
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /** 所属组，space.id, 数据库字段：space_id */
    private Long spaceId;

    /** 是否私有项目，1：是，0：否, 数据库字段：is_private */
    private Byte isPrivate;

    private List<Long> leaderIds;

    private Long creatorId;

    private Long modifierId;
}
