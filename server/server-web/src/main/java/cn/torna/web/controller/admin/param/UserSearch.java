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

    @Condition(ignore = true)
    private String id;

    @Condition(ignoreEmptyString = true, operator = Operator.like)
    private String username;

    /** 用户状态 0：禁用，1：启用(正常)，2：重设密码(未激活) */
    @Condition(operator = Operator.eq)
    private Byte status;

    /** 是否是超级管理员, 数据库字段：is_super_admin */
    @Condition(operator = Operator.eq)
    private Byte isSuperAdmin;
}
