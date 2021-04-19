package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tanghc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShareConfigParam extends PageParam {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;
}
