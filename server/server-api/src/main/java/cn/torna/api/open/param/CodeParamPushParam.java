package cn.torna.api.open.param;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.OperationMode;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class CodeParamPushParam {

    /** 字段名称 */
    @ApiDocField(name = "code", description = "参数名", required = true, example = "10001")
    @JSONField(name = "code")
    @NotBlank(message = "参数名不能为空")
    @Length(max = 50, message = "参数名不能超过50")
    private String name;

    /** 错误描述, 数据库字段：description */
    @ApiDocField(name = "msg", description = "错误描述", required = true, example = "token错误", maxLength = "256")
    @JSONField(name = "msg")
    @NotBlank(message = "错误描述不能为空")
    @Length(max = 256, message = "描述长度不能超过256")
    private String description;

    /** 解决方案 */
    @ApiDocField(name = "solution", description = "解决方案", required = true, example = "请填写token")
    @JSONField(name = "solution")
    @Length(max = 100, message = "描述长度不能超过100")
    private String example = "";

    private Byte createMode = OperationMode.OPEN.getType();

    private Byte modifyMode = OperationMode.OPEN.getType();

    private Byte isDeleted = Booleans.FALSE;

}
