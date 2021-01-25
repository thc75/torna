package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class CategoryAddParam {

    @NotBlank(message = "分类名称不能为空")
    @ApiDocField(description = "分类名称", required = true, example = "商品类目")
    @NotBlank(message = "分类名称不能为空")
    @Length(max = 50, message = "分类名称不能超过50")
    private String name;

}
