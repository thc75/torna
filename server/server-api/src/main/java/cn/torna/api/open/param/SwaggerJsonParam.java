package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author thc
 */
@Data
public class SwaggerJsonParam {

    @NotBlank(message = "推送人不能为空")
    @Length(max = 20, message = "推送人长度不能超过20")
    @ApiDocField(description = "推送人", example = "Jim")
    private String author;

    @NotBlank(message = "swagger json不能为空")
    @ApiDocField(description = "swagger json")
    private String json;
}
