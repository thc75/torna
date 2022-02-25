package cn.torna.web.controller.system.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class PropsParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long refId;
    private Byte type;
    private JSONObject props;

}
