package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DubboParam {

    @ApiDocField(description = "服务名称", example = "com.xx.doc.dubbo.DubboInterface")
    private String interfaceName;

    @ApiDocField(description = "author", example = "yu 2020/7/29.")
    private String author;

    @ApiDocField(description = "版本号", example = "1.0")
    private String version;

    @ApiDocField(description = "协议", example = "dubbo")
    private String protocol;

    @ApiDocField(description = "dependency", example = "<dependency>...</dependency>")
    private String dependency;
}
