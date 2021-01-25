package cn.torna.api.open.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class IdParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @NotNull(message = "文档ID不能为空")
    @ApiDocField(description = "文档id", required = true, dataType = DataType.STRING, example = "9VXEyXvg")
    private Long id;
}
