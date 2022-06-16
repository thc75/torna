package cn.torna.web.controller.user;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.UserInfoDTO;
import cn.torna.web.controller.user.param.UpdateInfoParam;
import cn.torna.web.controller.user.param.UpdatePasswordParam;
import cn.torna.web.controller.user.param.UserIdParam;
import cn.torna.web.controller.user.param.UserInfoSearchParam;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/list")
    public Result<List<UserInfoDTO>> pageUser(@RequestBody UserIdParam param) {
        Query query = Query.build(param);
        query.orderby("id", Sort.DESC);
        List<UserInfo> list = userInfoService.list(query);
        List<UserInfoDTO> userInfoDTOS = CopyUtil.copyList(list, UserInfoDTO::new);
        return Result.ok(userInfoDTOS);
    }

    @GetMapping("/get")
    public Result<UserInfoDTO> get() {
        User user = UserContext.getUser();
        UserInfo userInfo = userInfoService.getById(user.getUserId());
        UserInfoDTO userInfoDTO = CopyUtil.copyBean(userInfo, UserInfoDTO::new);
        return Result.ok(userInfoDTO);
    }

    @PostMapping("/update")
    public Result updateNickname(@RequestBody UpdateInfoParam param) {
        User user = UserContext.getUser();
        UserInfo userInfo = userInfoService.getById(user.getUserId());
        CopyUtil.copyPropertiesIgnoreNull(param, userInfo);
        userInfoService.update(userInfo);
        return Result.ok();
    }

    @PostMapping("/search")
    public Result<List<UserInfoDTO>> pageUser(@RequestBody UserInfoSearchParam param) {
        String username = param.getUsername();
        List<UserInfo> list;
        if (StringUtils.isEmpty(username)) {
            list = Collections.emptyList();
        } else {
            Query query = new Query()
                    .and(q ->
                            q.like("username", username)
                            .orLike("nickname", username)
                            .orLike("email", username)
                    ).orderby("id", Sort.DESC);
            list = userInfoService.list(query);
        }
        List<UserInfoDTO> userInfoDTOS = CopyUtil.copyList(list, UserInfoDTO::new);
        return Result.ok(userInfoDTOS);
    }

    /**
     * 修改密码
     * @param param
     * @return
     */
    @PostMapping("/password/update")
    public Result updatePassword(@RequestBody @Valid UpdatePasswordParam param) {
        long userId = UserContext.getUser().getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        if (UserInfoService.isThirdPartyUser(userInfo)) {
            throw new BizException("第三方账号无法修改密码");
        }
        // 演示账号禁止修改
        if ("guest@torna.cn".equalsIgnoreCase(userInfo.getUsername())) {
            return Result.ok();
        }
        String oldPwdHex = userInfoService.getDbPassword(userInfo.getUsername(), param.getOldPassword());
        Assert.isTrue(Objects.equals(oldPwdHex, userInfo.getPassword()), "旧密码错误");
        String newPwdHex = userInfoService.getDbPassword(userInfo.getUsername(), param.getPassword());
        userInfo.setPassword(newPwdHex);
        userInfoService.update(userInfo);
        return Result.ok();
    }


}