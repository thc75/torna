package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

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
