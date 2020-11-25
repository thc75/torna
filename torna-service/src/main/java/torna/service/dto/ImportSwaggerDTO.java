package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.bean.User;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class ImportSwaggerDTO {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    private String importUrl;

    /** basic认证用户名 */
    private String basicAuthUsername;
    /** basic认证密码 */
    private String basicAuthPassword;

    private User user;

}
