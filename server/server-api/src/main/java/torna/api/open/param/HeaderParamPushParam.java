package torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import torna.common.enums.OperationMode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class HeaderParamPushParam {

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "header名称", required = true, example = "token")
    @NotBlank(message = "header名称不能为空")
    @Length(max = 50)
    private String name;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    @NotNull(message = "是否必须不能为空")
    private Byte required;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", example = "iphone12")
    @Length(max = 100, message = "示例值长度不能超过100")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", example = "商品名称描述")
    @Length(max = 100, message = "描述长度不能超过100")
    private String description;

    private Byte createMode = OperationMode.OPEN.getType();

    private Byte modifyMode = OperationMode.OPEN.getType();

}
