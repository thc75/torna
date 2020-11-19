package torna.web.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class ImportSwaggerParam {
    @NotNull(message = "项目id不能为空")
    private Long projectId;

    @NotBlank(message = "导入URL不能为空")
    private String url;

    /** basic认证用户名 */
    private String basicAuthUsername;
    /** basic认证密码 */
    private String basicAuthPassword;

}
