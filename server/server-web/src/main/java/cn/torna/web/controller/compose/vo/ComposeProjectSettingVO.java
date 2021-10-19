package cn.torna.web.controller.compose.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class ComposeProjectSettingVO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private String gatewayUrl;

    private Byte showDebug;

    private List<ComposeProjectGlobalParamVO> globalParams;

    private List<ComposeProjectGlobalParamVO> globalReturns;
}
