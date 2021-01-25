package cn.torna.web.controller.admin.param;

import com.gitee.fastmybatis.core.query.Operator;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class UserSearch extends PageParam {

    @Condition(ignoreEmptyString = true, operator = Operator.like)
    private String username;
}
