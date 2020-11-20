package torna.web.controller;

import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class UserIdParam {

    @Condition(column = "id")
    @NotEmpty(message = "用户id不能为空")
    private List<Long> userIds;
}
