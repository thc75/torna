package cn.torna.web.controller.doc.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author thc
 */
@Data
public class DocListForm {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @Condition
    @NotNull
    private Long moduleId;

    @Condition
    private Byte status;

    /**
     * 是否过滤空文件夹
     */
    private Byte filterEmptyFolder;

}
