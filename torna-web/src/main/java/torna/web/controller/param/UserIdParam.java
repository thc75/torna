package torna.web.controller.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class UserIdParam {

    @Condition(column = "id")
    @NotEmpty(message = "用户id不能为空")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private List<Long> userIds;
}
