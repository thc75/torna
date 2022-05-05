package cn.torna.web.controller.doc.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 分享环境
 *
 * @author Joker
 * @since 1.0.0
 * @return
 */
@Data
public class ShareEnvironmentVO {

    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;


    /**
     * 分享配置id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long shareConfigId;

    /**
     * 模块环境id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleEnvironmentId;
}
