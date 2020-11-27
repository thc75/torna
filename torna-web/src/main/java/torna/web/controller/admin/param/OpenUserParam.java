package torna.web.controller.admin.param;

import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Data;

/**
 * 表名：open_user
 * 备注：开放用户
 *
 * @author tanghc
 */
@Data
public class OpenUserParam extends PageParam {

    /** appKey, 数据库字段：app_key */
    @Condition(ignoreEmptyString = true)
    private String appKey;

}