package torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class CategoryAddParam {

    @NotBlank(message = "分类名称不能为空")
    @ApiDocField(description = "分类名称", required = true)
    private String name;

}
