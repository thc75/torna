package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author thc
 */
@Data
@AllArgsConstructor
public class ShareDocDTO {
    /** 文档id, 数据库字段：doc_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 是否分享整个分类, 数据库字段：is_share_folder */
    private Byte isShareFolder;
}
