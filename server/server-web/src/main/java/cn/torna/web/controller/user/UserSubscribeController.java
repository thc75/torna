package cn.torna.web.controller.user;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserSubscribe;
import cn.torna.service.DocInfoService;
import cn.torna.service.UserSubscribeService;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.user.param.UserSubscribeParam;
import cn.torna.web.controller.user.vo.UserSubscribeVO;
import com.gitee.fastmybatis.core.query.param.PageParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("user/subscribe")
public class UserSubscribeController {

    @Autowired
    private UserSubscribeService userSubscribeService;

    @Autowired
    private DocInfoService docInfoService;

    @GetMapping("project/list")
    public Result<List<UserSubscribeVO>> listSubscribeProject() {
        User user = UserContext.getUser();
        List<UserSubscribe> userSubscribes = userSubscribeService.listUserSubscribe(user.getUserId(), UserSubscribeTypeEnum.PROJECT);
        List<UserSubscribeVO> userSubscribeDocVOS = CopyUtil.copyList(userSubscribes, UserSubscribeVO::new);
        return Result.ok(userSubscribeDocVOS);
    }

    @GetMapping("doc/page")
    public Result<PageEasyui<DocInfoVO>> pageSubscribeDoc(PageParam pageParam) {
        User user = UserContext.getUser();
        List<UserSubscribe> userSubscribes = userSubscribeService.listUserSubscribe(user.getUserId(), UserSubscribeTypeEnum.DOC);
        List<Long> docIdList = userSubscribes.stream()
                .map(UserSubscribe::getSourceId)
                .collect(Collectors.toList());
        PageEasyui docInfoPageEasyui = docInfoService.pageDocByIds(docIdList, pageParam);
        docInfoPageEasyui =  CopyUtil.copyPage(docInfoPageEasyui, DocInfoVO::new);
        return Result.ok(docInfoPageEasyui);
    }

    @GetMapping("doc/isSubscribe")
    public Result<Boolean> isSubscribeDoc(@HashId Long sourceId) {
        User user = UserContext.getUser();
        UserSubscribe userSubscribe = userSubscribeService.getSubscribe(user.getUserId(), UserSubscribeTypeEnum.DOC, sourceId);
        boolean isSubscribe = userSubscribe != null && userSubscribe.getIsDeleted() == Booleans.FALSE;
        return Result.ok(isSubscribe);
    }

    /**
     * 订阅文档
     *
     * @param param param
     */
    @PostMapping("doc/subscribe")
    public Result subscribeDoc(@RequestBody UserSubscribeParam param) {
        User user = UserContext.getUser();
        userSubscribeService.subscribe(user.getUserId(), UserSubscribeTypeEnum.DOC, param.getSourceId());
        return Result.ok();
    }

    /**
     * 订阅文档
     *
     * @param param param
     */
    @PostMapping("doc/cancelSubscribe")
    public Result cancelSubscribe(@RequestBody UserSubscribeParam param) {
        User user = UserContext.getUser();
        userSubscribeService.cancelSubscribe(user.getUserId(), UserSubscribeTypeEnum.DOC, param.getSourceId());
        return Result.ok();
    }

    /**
     * 订阅项目
     *
     * @param param param
     */
    @PostMapping("project/subscribe")
    public Result subscribeProject(@RequestBody UserSubscribeParam param) {
        User user = UserContext.getUser();
        userSubscribeService.subscribe(user.getUserId(), UserSubscribeTypeEnum.PROJECT, param.getSourceId());
        return Result.ok();
    }

}