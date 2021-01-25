package cn.torna.web.controller.user.param;

import com.gitee.fastmybatis.core.query.Operator;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class UserInfoSearchParam {

    @Condition(ignoreEmptyString = true, operator = Operator.like)
    private String username;
}
