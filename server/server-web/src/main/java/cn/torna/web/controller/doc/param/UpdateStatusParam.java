package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class UpdateStatusParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private Byte status;

}
