package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectUpdateParam {
    @NotNull(message = "项目ID不能为空")
    private Long id;

    /** 项目名称, 数据库字段：name */
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /** 项目描述, 数据库字段：description */
    private String description;

    /** 是否私有项目，1：是，0：否, 数据库字段：is_private */
    @NotNull
    private Byte isPrivate;
}
