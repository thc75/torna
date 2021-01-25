package cn.torna.api.open.result;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocCategoryResult {
    @ApiDocField(description = "分类id", maxLength = "12", example = "9VXEyXvg")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 文档名称, 数据库字段：name */
    @ApiDocField(description = "分类名称", maxLength = "50", example = "商品分类")
    private String name;
}
