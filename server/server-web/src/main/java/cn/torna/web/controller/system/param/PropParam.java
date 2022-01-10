package cn.torna.web.controller.system.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class PropParam {
    /** 关联id, 数据库字段：ref_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long refId;

    /** 类型，0：doc_info属性, 数据库字段：type */
    private Byte type;

    /**  数据库字段：name */
    private String name;

    /**  数据库字段：val */
    private String val;

}
