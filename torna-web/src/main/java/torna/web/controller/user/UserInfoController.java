package torna.web.controller.user;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.context.UserContext;
import torna.common.util.CopyUtil;
import torna.dao.entity.UserInfo;
import torna.service.UserInfoService;
import torna.service.dto.UserInfoDTO;
import torna.web.controller.user.param.UpdatePasswordParam;
import torna.web.controller.user.param.UserIdParam;
import torna.web.controller.user.param.UserInfoSearchParam;

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
    public Result<List<UserInfoDTO>> pageUser(@RequestBody @Valid UserIdParam param) {
        Query query = Query.build(param);
        List<UserInfo> list = userInfoService.list(query);
        List<UserInfoDTO> userInfoDTOS = CopyUtil.copyList(list, UserInfoDTO::new);
        return Result.ok(userInfoDTOS);
    }

    @PostMapping("/search")
    public Result<List<UserInfoDTO>> pageUser(@RequestBody UserInfoSearchParam param) {
        String username = param.getUsername();
        if (StringUtils.isEmpty(username)) {
            return Result.ok(Collections.emptyList());
        }
        Query query = Query.build(param).setQueryAll(true);
        List<UserInfo> list = userInfoService.list(query);
        List<UserInfoDTO> userInfoDTOS = CopyUtil.copyList(list, UserInfoDTO::new);
        return Result.ok(userInfoDTOS);
    }

    /**
     * 修改密码
     * @param param
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody @Valid UpdatePasswordParam param) {
        long userId = UserContext.getUser().getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        String oldPwdHex = userInfoService.getDbPassword(userInfo.getUsername(), param.getOldPassword());
        Assert.isTrue(Objects.equals(oldPwdHex, userInfo.getPassword()), "旧密码错误");
        String newPwdHex = userInfoService.getDbPassword(userInfo.getUsername(), param.getPassword());
        userInfo.setPassword(newPwdHex);
        userInfoService.update(userInfo);
        return Result.ok();
    }


}