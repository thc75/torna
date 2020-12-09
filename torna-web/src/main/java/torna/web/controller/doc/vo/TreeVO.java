package torna.web.controller.doc.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

/**
 * @author tanghc
 */
@Data
public class TreeVO {

    private String id;

    private String label;

    private String parentId;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    private String url;

    private Byte type;

    public TreeVO(String id, String label, String parentId, byte type) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
        this.type = type;
    }

}
