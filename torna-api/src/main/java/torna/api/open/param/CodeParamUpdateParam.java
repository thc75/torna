package torna.api.open.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class CodeParamUpdateParam extends CodeParamCreateParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @ApiDocField(description = "参数id", example = "asdf", dataType = DataType.STRING)
    private Long id;
}
