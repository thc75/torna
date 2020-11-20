package torna.web.controller;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.bean.Result;
import torna.common.util.CopyUtil;
import torna.dao.entity.UserInfo;
import torna.service.UserInfoService;
import torna.service.dto.UserInfoDTO;
import torna.web.controller.param.UserInfoSearchParam;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("/user")
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



}