package cn.torna.web.controller.user;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserMessage;
import cn.torna.service.UserMessageService;
import cn.torna.web.config.UserContext;
import cn.torna.web.controller.system.param.IdParam;
import cn.torna.web.controller.user.vo.UserMessageVO;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.param.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("user/message")
public class UserMessageController {

    @Autowired
    private UserMessageService userMessageService;

    /**
     * 查询未读信息
     */
    @GetMapping("unread")
    public Result<List<UserMessageVO>> unread(@RequestParam(defaultValue = "99") int limit) {
        User user = UserContext.getUser();
        List<UserMessage> userMessages = userMessageService.listUserUnReadMessage(user.getUserId(), limit);
        List<UserMessageVO> userMessageVOS = CopyUtil.copyList(userMessages, UserMessageVO::new);
        return Result.ok(userMessageVOS);
    }

    @PostMapping("page")
    public Result<PageInfo<UserMessageVO>> page(@RequestBody PageParam param) {
        User user = UserContext.getUser();
        PageInfo<UserMessage> pageEasyui = userMessageService.pageMessage(user.getUserId(), param);
        PageInfo copyPage = CopyUtil.copyPage(pageEasyui, UserMessageVO::new);
        return Result.ok(copyPage);
    }

    @PostMapping("setRead")
    public Result setRead(@RequestBody IdParam param) {
        userMessageService.setRead(param.getId());
        return Result.ok();
    }

    @PostMapping("setReadAll")
    public Result setReadAll() {
        User user = UserContext.getUser();
        userMessageService.setReadAll(user.getUserId());
        return Result.ok();
    }

    @PostMapping("deletePushMessage")
    public Result deletePushMessage() {
        User user = UserContext.getUser();
        userMessageService.deletePushMessage(user.getUserId());
        return Result.ok();
    }


}
