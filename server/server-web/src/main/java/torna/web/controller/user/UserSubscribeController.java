package torna.web.controller.user;

import torna.common.bean.Result;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.UserSubscribeTypeEnum;
import torna.common.util.CopyUtil;
import torna.dao.entity.UserSubscribe;
import torna.service.UserSubscribeService;
import torna.web.controller.user.vo.UserSubscribeVO;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("user/subscribe")
public class UserSubscribeController {

    @Autowired
    private UserSubscribeService userSubscribeService;

    @RequestMapping("project/list")
    public Result<List<UserSubscribeVO>> listSubscribeProject() {
        User user = UserContext.getUser();
        List<UserSubscribe> userSubscribes = userSubscribeService.listUserSubscribe(user.getUserId(), UserSubscribeTypeEnum.PROJECT);
        List<UserSubscribeVO> userSubscribeDocVOS = CopyUtil.copyList(userSubscribes, UserSubscribeVO::new);
        return Result.ok(userSubscribeDocVOS);
    }

    @RequestMapping("doc/list")
    public Result<List<UserSubscribeVO>> listSubscribeDoc() {
        User user = UserContext.getUser();
        List<UserSubscribe> userSubscribes = userSubscribeService.listUserSubscribe(user.getUserId(), UserSubscribeTypeEnum.DOC);
        List<UserSubscribeVO> userSubscribeDocVOS = CopyUtil.copyList(userSubscribes, UserSubscribeVO::new);
        return Result.ok(userSubscribeDocVOS);
    }

    /**
     * 订阅文档
     *
     * @param sourceId sourceId
     */
    @RequestMapping("doc/subscribe")
    public Result subscribeDoc(Long sourceId) {
        User user = UserContext.getUser();
        userSubscribeService.subscribe(user.getUserId(), UserSubscribeTypeEnum.DOC, sourceId);
        return Result.ok();
    }

    /**
     * 订阅项目
     *
     * @param sourceId sourceId
     */
    @RequestMapping("project/subscribe")
    public Result subscribeProject(Long sourceId) {
        User user = UserContext.getUser();
        userSubscribeService.subscribe(user.getUserId(), UserSubscribeTypeEnum.PROJECT, sourceId);
        return Result.ok();
    }

}