package cn.torna.web.controller.openuser.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 表名：open_user
 * 备注：开放用户
 *
 * @author tanghc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenUserParam extends PageParam {

    @NotNull
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long spaceId;

    /** appKey, 数据库字段：app_key */
    @Condition(ignoreEmptyString = true)
    private String appKey;

}