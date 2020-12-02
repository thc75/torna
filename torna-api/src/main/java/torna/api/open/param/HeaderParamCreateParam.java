package torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.enums.OperationMode;

/**
 * @author tanghc
 */
@Data
public class HeaderParamCreateParam {

    /** 字段名称, 数据库字段：name */
    @ApiDocField(description = "header名称", required = true, example = "token")
    private String name;

    /** 是否必须，1：是，0：否, 数据库字段：required */
    @ApiDocField(description = "是否必须，1：是，0：否", required = true, example = "1")
    private Byte required;

    /** 示例值, 数据库字段：example */
    @ApiDocField(description = "示例值", example = "asdddxs")
    private String example;

    /** 描述, 数据库字段：description */
    @ApiDocField(description = "描述", example = "请求token")
    private String description;

    private Byte createMode = OperationMode.OPEN.getType();

    private Byte modifyMode = OperationMode.OPEN.getType();

}
