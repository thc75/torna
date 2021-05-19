package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class EnumInfoCreateBatchParam {

    @ApiDocField(description = "枚举列表", required = true, elementClass = EnumInfoCreateParam.class)
    @NotEmpty(message = "enums不能为空")
    private List<EnumInfoCreateParam> enums;

}
