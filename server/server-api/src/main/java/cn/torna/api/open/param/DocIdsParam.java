package cn.torna.api.open.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import java.util.List;

/**
 * @author 六如
 */
@Data
public class DocIdsParam {

    @ApiDocField(name = "文档id", required = true)
    private List<Long> docIds;


}
