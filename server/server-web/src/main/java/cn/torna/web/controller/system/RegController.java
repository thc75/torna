package cn.torna.web.controller.system;

import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.service.UserInfoService;
import cn.torna.web.controller.system.param.RegParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.NoLogin;
import cn.torna.service.dto.UserAddDTO;

import javax.validation.Valid;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
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
