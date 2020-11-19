package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ImportSwaggerDTO {
    private Long projectId;

    private String url;

    /** basic认证用户名 */
    private String basicAuthUsername;
    /** basic认证密码 */
    private String basicAuthPassword;

}
