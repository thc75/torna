package cn.torna.web.controller.system.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author thc
 */
@Data
public class PropSearchParam {

    @NotEmpty
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> refIds;

}
