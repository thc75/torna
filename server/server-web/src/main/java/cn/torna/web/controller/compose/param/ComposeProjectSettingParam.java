package cn.torna.web.controller.compose.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ComposeProjectSettingParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private String gatewayUrl;

    private Byte showDebug;
}
