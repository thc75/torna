package cn.torna.api.open.result;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocCodeResult {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "参数id", example = "asdf", dataType = DataType.STRING)
    private Long id;

    /** 字段名称 */
    @ApiDocField(name = "code", description = "参数名", required = true, example = "10001")
    @JSONField(name = "code")
    private String name;

    /** 错误描述, 数据库字段：description */
    @ApiDocField(name = "msg", description = "错误描述", example = "token错误")
    @JSONField(name = "msg")
    private String description;

    /** 解决方案 */
    @ApiDocField(name = "solution", description = "解决方案", example = "请填写token")
    @JSONField(name = "solution")
    private String example;

}
