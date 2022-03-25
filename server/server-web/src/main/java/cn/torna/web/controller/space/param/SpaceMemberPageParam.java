package cn.torna.web.controller.space.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Getter
@Setter
public class SpaceMemberPageParam extends PageParam  {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    @Condition(ignore = true)
    private Long spaceId;

    @Condition(ignore = true)
    private String username;

}
