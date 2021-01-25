package cn.torna.web.controller.module.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ModuleAddParam {

    /** 模块名称, 数据库字段：name */
    @NotBlank(message = "模块名称不能为空")
    private String name;

    /** project.id, 数据库字段：project_id */
    @NotNull(message = "项目id不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

}
