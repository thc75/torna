package cn.torna.api.open.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class CategoryUpdateParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @NotNull(message = "文档ID不能为空")
    @ApiDocField(description = "文档id", required = true, dataType = DataType.STRING, example = "9VXEyXvg")
    private Long id;

    @ApiDocField(description = "分类名称", required = true, example = "商品分类")
    @NotBlank(message = "分类名称不能为空")
    @Length(max = 50, message = "分类名称不能超过50")
    private String name;

}
