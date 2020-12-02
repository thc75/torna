package torna.api.open.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.enums.OperationMode;

/**
 * @author tanghc
 */
@Data
public class CodeParamCreateParam {

    /** 字段名称 */
    @ApiDocField(name = "code", description = "参数名", required = true, example = "10001")
    @JSONField(name = "code")
    private String name;

    /** 错误描述, 数据库字段：description */
    @ApiDocField(name = "msg", description = "错误描述", required = true, example = "token错误")
    @JSONField(name = "msg")
    private String description;

    /** 解决方案 */
    @ApiDocField(name = "solution", description = "解决方案", required = true, example = "请填写token")
    @JSONField(name = "solution")
    private String example;

    private Byte createMode = OperationMode.OPEN.getType();

    private Byte modifyMode = OperationMode.OPEN.getType();

}
