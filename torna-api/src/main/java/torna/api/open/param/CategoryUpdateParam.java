package torna.api.open.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class CategoryUpdateParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @NotNull(message = "文档ID不能为空")
    @ApiDocField(description = "文档id", required = true, dataType = DataType.STRING)
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @ApiDocField(description = "分类名称", required = true)
    private String name;

}
