package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocPushParam {

    /** baseUrl，如：http://localhost:8080 */
    @ApiDocField(description = "baseUrl", required = true, example = "http://localhost:8080")
    @NotBlank(message = "baseUrl不能为空")
    @Length(max = 100, message = "baseUrl长度不能超过100")
    private String baseUrl;

    @ApiDocField(description = "api列表", required = true, elementClass = DocPushItemParam.class)
    @NotEmpty(message = "文档内容不能为空")
    private List<DocPushItemParam> apis;

}
