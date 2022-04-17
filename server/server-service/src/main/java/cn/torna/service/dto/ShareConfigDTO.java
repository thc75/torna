package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ShareConfigDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;
    private Byte type;
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;
    private Byte isAll;
    private List<Content> content;
    private String remark;
    /** 是否显示调试 */
    private Byte isShowDebug;

    /**
     * 调试环境是否全选， 1-全选， 0-不选
     */
    private Byte isAllSelectedDebug;

    /**
     * 模块环境id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> moduleEnvironmentIdList;

    @Data
    public static class Content {
        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private Long docId;
        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private Long parentId;
        private Byte isShareFolder;
    }
}
