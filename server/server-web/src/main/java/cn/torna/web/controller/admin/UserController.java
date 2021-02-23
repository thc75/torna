package cn.torna.web.controller.admin;

import cn.torna.common.bean.Result;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.UserAddDTO;
import cn.torna.service.dto.UserInfoDTO;
import cn.torna.web.controller.admin.param.ResetPasswordParam;
import cn.torna.web.controller.admin.param.UserCreateParam;
import cn.torna.web.controller.admin.param.UserInfoParam;
import cn.torna.web.controller.admin.param.UserSearch;
import cn.torna.web.controller.admin.vo.UserCreateVO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Value("${torna.user.initial-password}")
    private String initPassword;

    @PostMapping("page")
    public Result<PageEasyui<UserInfoDTO>> page(@RequestBody UserSearch userSearch) {
        Query query = userSearch.toQuery();
        PageEasyui<UserInfoDTO> page = MapperUtil.queryForEasyuiDatagrid(userInfoService.getMapper(), query, UserInfoDTO.class);
        return Result.ok(page);
    }

    @PostMapping("create")
    public Result<UserCreateVO> create(@RequestBody UserCreateParam param) {
        UserAddDTO userAddDTO = CopyUtil.copyBean(param, UserAddDTO::new);
        userAddDTO.setPassword(DigestUtils.md5DigestAsHex(initPassword.getBytes(StandardCharsets.UTF_8)));
        userAddDTO.setStatus(UserStatusEnum.SET_PASSWORD.getStatus());
        userInfoService.addUser(userAddDTO);
        UserCreateVO userCreateVO = new UserCreateVO();
        userCreateVO.setUsername(param.getUsername());
        userCreateVO.setPassword(initPassword);
        return Result.ok(userCreateVO);
    }

    @PostMapping("disable")
    public Result disable(@RequestBody UserInfoParam param) {
        Long id = param.getId();
        userInfoService.disableUser(id);
        return Result.ok();
    }

    @PostMapping("enable")
    public Result enable(@RequestBody UserInfoParam param) {
        Long id = param.getId();
        userInfoService.enableUser(id);
        return Result.ok();
    }

    @PostMapping("password/reset")
    public Result<String> resetPwd(@RequestBody ResetPasswordParam param) {
        Long id = param.getId();
        String resetPassword = userInfoService.resetPassword(id);
        return Result.ok(resetPassword);
    }

}