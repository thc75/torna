package cn.torna.api.open.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import java.util.List;

/**
 * @author 六如
 */
@Data
public class DocResult {

    @ApiDocField(name = "文档")
    private List<DocInfoResult> data;

}
