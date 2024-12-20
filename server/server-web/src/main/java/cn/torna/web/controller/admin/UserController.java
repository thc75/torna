package cn.torna.web.controller.admin;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.ProjectUser;
import cn.torna.dao.entity.UserInfo;
import cn.torna.service.AllocateProjectService;
import cn.torna.service.ProjectService;
import cn.torna.service.UserInfoService;
import cn.torna.service.dto.AllocateProjectDTO;
import cn.torna.service.dto.UserAddDTO;
import cn.torna.service.dto.UserInfoDTO;
import cn.torna.web.controller.admin.param.AllocateProjectParam;
import cn.torna.web.controller.admin.param.ResetPasswordParam;
import cn.torna.web.controller.admin.param.UserCreateParam;
import cn.torna.web.controller.admin.param.UserInfoParam;
import cn.torna.web.controller.admin.param.UserSearch;
import cn.torna.web.controller.admin.param.UserUpdateParam;
import cn.torna.web.controller.admin.vo.UserCreateVO;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AllocateProjectService allocateProjectService;

    @Autowired
    private ProjectService projectService;

    @Value("${torna.user.initial-password}")
    private String initPassword;

    @PostMapping("page")
    public Result<PageInfo<UserInfoDTO>> page(@RequestBody UserSearch userSearch) {
        LambdaQuery<UserInfo> query = userSearch.toLambdaQuery(UserInfo.class);
        String id = userSearch.getId();
        if (StringUtils.hasText(id)) {
            Long userId = IdUtil.decode(id);
            // 没有查到，指定一个查不到的值
            if (userId == null) {
                userId = -100L;
            }
            query.eq(UserInfo::getId, userId);
        }
        query.orderBy(UserInfo::getId, Sort.DESC);
        PageInfo<UserInfoDTO> page = userInfoService.pageAndConvert(query, list -> CopyUtil.copyList(list, UserInfoDTO::new));
        return Result.ok(page);
    }

    @PostMapping("create")
    public Result<UserCreateVO> create(@RequestBody UserCreateParam param) {
        UserAddDTO userAddDTO = CopyUtil.copyBean(param, UserAddDTO::new);
        userAddDTO.setPassword(DigestUtils.md5DigestAsHex(initPassword.getBytes(StandardCharsets.UTF_8)));
        userAddDTO.setStatus(UserStatusEnum.SET_PASSWORD.getStatus());
        userAddDTO.setSourceEnum(UserInfoSourceEnum.BACKEND);
        userInfoService.addUser(userAddDTO);
        UserCreateVO userCreateVO = new UserCreateVO();
        userCreateVO.setUsername(param.getUsername());
        userCreateVO.setPassword(initPassword);
        return Result.ok(userCreateVO);
    }

    @PostMapping("update")
    public Result update(@RequestBody UserUpdateParam param) {
        String email = param.getUsername();
        UserInfo exist = userInfoService.getByUsername(email);
        if (exist != null && !Objects.equals(param.getId(), exist.getId())) {
            throw new BizException("该邮箱已存在");
        }
        UserInfo userInfo = userInfoService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, userInfo);
        // 简单判断邮箱
        String username = userInfo.getUsername();
        if (username.contains("@") && StringUtils.isEmpty(userInfo.getEmail())) {
            userInfo.setEmail(username);
        }
        userInfoService.update(userInfo);
        return Result.ok();
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

    @PostMapping("delete")
    public Result delete(@RequestBody UserInfoParam param) {
        Long id = param.getId();
        userInfoService.getMapper().forceDeleteById(id);
        return Result.ok();
    }

    @PostMapping("password/reset")
    public Result<String> resetPwd(@RequestBody ResetPasswordParam param) {
        Long id = param.getId();
        String resetPassword = userInfoService.resetPassword(id);
        return Result.ok(resetPassword);
    }

    @PostMapping("allocateProject")
    public Result allocateProject(@RequestBody AllocateProjectParam param) {
        AllocateProjectDTO allocateProjectDTO = CopyUtil.copyBean(param, AllocateProjectDTO::new);
        allocateProjectService.allocateProject(allocateProjectDTO);
        return Result.ok();
    }

    @GetMapping("getUserProjectIds")
    public Result<List<String>> getUserProjectIds(@HashId Long userId) {
        List<String> projectIds = projectService.listUserProject(userId)
                .stream()
                .map(ProjectUser::getProjectId)
                .map(IdUtil::encode)
                .collect(Collectors.toList());
        return Result.ok(projectIds);
    }

}
