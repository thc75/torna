package cn.torna.web.controller.system.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author thc
 */
@Data
public class ConfigUpdateParam {
    @NotBlank
    private String key;

    @NotNull
    private String value;

    @Length(max = 128)
    private String remark;
}
