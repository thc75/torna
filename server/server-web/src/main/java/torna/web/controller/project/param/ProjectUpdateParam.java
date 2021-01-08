package torna.web.controller.project.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ProjectUpdateParam {
    @NotNull(message = "项目ID不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
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
