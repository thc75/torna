package cn.torna.web.controller.system;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.UserAddDTO;
import cn.torna.web.controller.system.param.RegParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Value("${torna.register.enable}")
    private boolean enableReg;

    @PostMapping("/reg")
    public Result reg(@RequestBody @Valid RegParam param) {
        if (!enableReg) {
            throw new BizException("不允许注册");
        }
        if (EnvironmentKeys.LOGIN_THIRD_PARTY_ENABLE.getBoolean()) {
            throw new BizException("不允许注册，请使用第三方登录");
        }
        // 创建用户
        UserAddDTO userAddDTO = CopyUtil.copyBean(param, UserAddDTO::new);
        userAddDTO.setIsSuperAdmin(Booleans.FALSE);
        userAddDTO.setStatus(UserStatusEnum.ENABLE.getStatus());
        userAddDTO.setSourceEnum(UserInfoSourceEnum.REGISTER);
        // 注册用户不是超级管理员
        userInfoService.addUser(userAddDTO);
        return Result.ok();
    }

}
