package cn.torna.api.open.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ModuleResult {

    @ApiDocField(name = "应用id")
    private Long id;

    @ApiDocField(name = "应用名称")
    private String name;

}
