package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class EnumInfoCreateParam {

    /** 枚举名称, 数据库字段：name */
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称长度不能超过100")
    @ApiDocField(description = "字典分类名称", required = true, example = "支付枚举")
    private String name;

    /** 枚举说明, 数据库字段：description */
    @Length(max = 100, message = "描述长度不能超过100")
    @ApiDocField(description = "字典分类描述", example = "支付状态")
    private String description;

    @NotEmpty(message = "枚举项不能为空")
    @ApiDocField(description = "枚举项", required = true, elementClass = EnumItemCreateParam.class)
    private List<EnumItemCreateParam> items;
}
