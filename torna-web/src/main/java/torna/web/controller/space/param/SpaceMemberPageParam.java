package torna.web.controller.space.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.annotation.ConditionConfig;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class SpaceMemberPageParam extends PageParam  {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @Condition(ignore = true)
    private Long spaceId;

    @Condition(ignoreEmptyString = true)
    private String username;

}
