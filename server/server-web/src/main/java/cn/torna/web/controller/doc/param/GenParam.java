package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 六如
 */
@Data
public class GenParam {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long templateId;

}
