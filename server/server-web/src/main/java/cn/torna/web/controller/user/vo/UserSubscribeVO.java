package cn.torna.web.controller.user.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class UserSubscribeVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long sourceId;

    private Date gmtModified;
}
