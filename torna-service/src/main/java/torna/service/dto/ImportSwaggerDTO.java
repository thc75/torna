package torna.service.dto;

import lombok.Data;
import torna.common.bean.User;

/**
 * @author tanghc
 */
@Data
public class ImportSwaggerDTO {
    private Long projectId;

    private String importUrl;

    /** basic认证用户名 */
    private String basicAuthUsername;
    /** basic认证密码 */
    private String basicAuthPassword;

    private User user;

}
