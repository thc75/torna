package cn.torna.web.controller.admin.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class AllocateProjectParam {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long userId;

    private String role;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> projectIds;

}
