package cn.torna.web.controller.compose.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ComposeDocAddParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    private List<Doc> docList;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    @Data
    public static class Doc {
        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private Long docId;
        private String origin;
        private Integer orderIndex;
    }

}
