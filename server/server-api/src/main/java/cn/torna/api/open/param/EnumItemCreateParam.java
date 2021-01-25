package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class EnumItemCreateParam {

    /** 名称，字面值, 数据库字段：name */
    @NotBlank(message = "枚举名称不能为空")
    @Length(max = 100, message = "枚举名称长度不能超过100")
    @ApiDocField(description = "枚举名称", required = true, example = "WAIT_PAY")
    private String name;

    /** 类型, 数据库字段：type */
    @NotBlank(message = "枚举类型不能为空")
    @Length(max = 50, message = "枚举类型长度不能超过50")
    @ApiDocField(description = "枚举类型，string/number/boolean三选一", required = true, example = "string")
    private String type;

    /** 枚举值, 数据库字段：value */
    @NotBlank(message = "枚举值不能为空")
    @Length(max = 50, message = "枚举值长度不能超过50")
    @ApiDocField(description = "枚举值", required = true, example = "0")
    private String value;

    /** 枚举描述, 数据库字段：description */
    @Length(max = 100, message = "枚举值描述长度不能超过100")
    @ApiDocField(description = "枚举值描述", required = true, example = "未支付")
    private String description = "";
}