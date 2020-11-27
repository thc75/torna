package torna.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.NoLogin;
import torna.common.bean.Result;
import torna.common.util.CopyUtil;
import torna.dao.entity.UserInfo;
import torna.service.UserInfoService;
import torna.service.dto.UserAddDTO;
import torna.web.controller.system.param.RegParam;

import javax.validation.Valid;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("common")
@NoLogin
public class RegController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/reg")
    public Result reg(@RequestBody @Valid RegParam param) {
        // 校验邮箱是否存在
        this.checkEmail(param.getUsername());
        // 创建用户
        UserAddDTO userAddDTO = CopyUtil.copyBean(param, UserAddDTO::new);
        // 注册用户不是超级管理员
        userInfoService.addUser(userAddDTO);
        return Result.ok();
    }

    private void checkEmail(String email) {
        UserInfo userInfo = userInfoService.get("username", email);
        Assert.isNull(userInfo, () -> "该邮箱已被注册");
    }

}
