package cn.torna.web.controller.space.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;

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
