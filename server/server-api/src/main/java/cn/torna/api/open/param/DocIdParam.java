package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 六如
 */
@Data
public class DocIdParam {

    @ApiDocField(name = "文档id", required = true)
    @NotNull
    private Long docId;

    private List<Long> docIds;
}
