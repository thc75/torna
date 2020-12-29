package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.bean.User;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class ImportPostmanDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    private String json;

    private User user;

}
