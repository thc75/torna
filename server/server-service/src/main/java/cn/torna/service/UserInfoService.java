package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.GenerateUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.JwtUtil;
import cn.torna.common.util.PasswordUtil;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.UserInfoMapper;
import cn.torna.service.dto.DingTalkLoginDTO;
import cn.torna.service.dto.LoginDTO;
import cn.torna.service.dto.UserAddDTO;
import cn.torna.service.dto.UserInfoDTO;
import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.ThirdPartyLoginManager;
import cn.torna.service.login.form.impl.DefaultThirdPartyLoginManager;
import cn.torna.service.login.form.impl.LdapLoginManager;
import com.gitee.fastmybatis.core.query.Query;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class UserInfoService extends BaseService<UserInfo, UserInfoMapper> {

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${torna.password.salt:@3dG%gm^uu&=.}")
    private String salt;

    @Value("${torna.jwt.timeout-days:365}")
    private int jwtTimeoutDays;

    @Value("${torna.jwt.secret:CHezCvjte^WHy5^#MqSVx9A%6.F$eV}")
    private String jwtSecret;

    @Autowired
    private DefaultThirdPartyLoginManager defaultThirdPartyLoginManager;

    @Autowired
    private LdapLoginManager ldapLoginManager;

    @Autowired
    private UserDingtalkInfoService userDingtalkInfoService;

    /**
     * 是否是第三方用户
     * @param userInfo user
     * @return true，第三方用户
     */
    public static boolean isThirdPartyUser(UserInfo userInfo) {
        String source = userInfo.getSource();
        return Objects.equals(source, UserInfoSourceEnum.FORM.getSource())
                || Objects.equals(source, UserInfoSourceEnum.OAUTH.getSource())
                || Objects.equals(source, UserInfoSourceEnum.LDAP.getSource());
    }

    /**
     * 添加新用户，用于注册
     *
     * @param userAddDTO 用户信息
     */
    public void addUser(UserAddDTO userAddDTO) {
        String username = userAddDTO.getUsername();
        // 校验邮箱是否存在
        checkEmail(username);
        UserInfo userInfo = CopyUtil.copyBean(userAddDTO, UserInfo::new);
        // 简单判断邮箱 :)
        if (username.contains("@")) {
            userInfo.setEmail(username);
        }
        userInfo.setSource(userAddDTO.getSourceEnum().getSource());
        String password = getDbPassword(username, userAddDTO.getPassword());
        userInfo.setPassword(password);
        this.save(userInfo);
    }

    public UserInfo getByUsername(String username) {
        return get("username", username);
    }

    public void checkEmail(String email) {
        UserInfo userInfo = get("username", email);
        Assert.isNull(userInfo, () -> "账号已被注册");
    }


    public String getDbPassword(String username, String password) {
        return GenerateUtil.getUserPassword(username, password, salt);
    }

    public List<UserInfoDTO> listUserInfo(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        Query query = new Query()
                .in("id", userIds)
                .setQueryAll(true);
        List<UserInfo> list = this.list(query);
        return CopyUtil.copyList(list, UserInfoDTO::new);
    }

    public <T> void checkExist(List<T> existUsers, Function<T, Long> userIdGetter) {
        if (CollectionUtils.isEmpty(existUsers)) {
            return;
        }
        List<Long> userIdList = CopyUtil.copyList(existUsers, userIdGetter);
        Query query = new Query().in("id", userIdList).setQueryAll(true);
        List<UserInfo> list = list(query);
        List<String> nicknames = CopyUtil.copyList(list, UserInfo::getNickname);
        throw new BizException(String.join("、", nicknames) + " 已存在");
    }

    public LoginUser login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        UserInfoSourceEnum userInfoSourceEnum = loginDTO.getUserInfoSourceEnum();
        UserInfo userInfo;
        switch (userInfoSourceEnum) {
            case FORM:
                // 第三方表单登录
                userInfo = this.doThirdPartyLogin(defaultThirdPartyLoginManager, username, password);
                break;
            case LDAP:
                // LDAP登录
                userInfo = this.doThirdPartyLogin(ldapLoginManager, username, password);
                break;
            default:{
                // 默认注册账号登录
                userInfo = this.doDatabaseLogin(username, password);
            }
        }
        LoginUser loginUser = buildLoginUser(userInfo);
        // 保存到缓存
        userCacheManager.saveUser(loginUser);
        return loginUser;
    }

    private LoginUser buildLoginUser(UserInfo userInfo) {
        if (UserStatusEnum.of(userInfo.getStatus()) == UserStatusEnum.DISABLED) {
            throw new BizException("账号已禁用，请联系管理员");
        }
        // 登录成功
        LoginUser loginUser = CopyUtil.copyBean(userInfo, LoginUser::new);
        // 创建token
        String token = this.createToken(userInfo.getId());
        loginUser.setToken(token);
        return loginUser;
    }

    /**
     * oauth登录
     * @param user 认证用户
     * @return 返回登录用户
     */
    public LoginUser oauthLogin(AuthUser user) {
        UserInfo userInfo = getByUsername(user.getUsername());
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername(user.getUsername());
            userInfo.setPassword(GenerateUtil.getUUID());
            userInfo.setNickname(user.getNickname());
            userInfo.setIsSuperAdmin(Booleans.FALSE);
            userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
            userInfo.setIsDeleted(Booleans.FALSE);
            userInfo.setSource(UserInfoSourceEnum.OAUTH.getSource());
            userInfo.setEmail(user.getEmail());
            this.save(userInfo);
        }
        return buildLoginUser(userInfo);
    }

    /**
     * 钉钉登录
     * @param dingTalkLoginDTO
     * @return
     */
    public LoginUser dingtalkLogin(DingTalkLoginDTO dingTalkLoginDTO) {
        String username = dingTalkLoginDTO.getUnionid();
        UserInfo userInfo = getByUsername(username);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setPassword(GenerateUtil.getUUID());
            userInfo.setNickname(dingTalkLoginDTO.getName());
            // 给老板设置为超管
            userInfo.setIsSuperAdmin(Booleans.toValue(dingTalkLoginDTO.getBoss()));
            userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
            userInfo.setIsDeleted(Booleans.FALSE);
            userInfo.setSource(UserInfoSourceEnum.OAUTH.getSource());
            userInfo.setEmail(dingTalkLoginDTO.getEmail());
            this.save(userInfo);
        }
        userDingtalkInfoService.addUser(dingTalkLoginDTO, userInfo);
        return buildLoginUser(userInfo);
    }

    private UserInfo doThirdPartyLogin(ThirdPartyLoginManager thirdPartyLoginManager, String username, String password) {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);
        LoginResult loginResult;
        try {
            loginResult = thirdPartyLoginManager.login(loginForm);
        } catch (Exception e) {
            log.error("第三方登录失败", e);
            throw new BizException(e.getMessage());
        }

        UserInfo userInfo = getByUsername(username);

        // 用户第一次登录则插入到user_info表
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setPassword(GenerateUtil.getUUID());
            userInfo.setNickname(loginResult.getNickname());
            userInfo.setIsSuperAdmin(Booleans.FALSE);
            userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
            userInfo.setIsDeleted(Booleans.FALSE);
            userInfo.setSource(loginResult.getUserInfoSourceEnum().getSource());
            userInfo.setEmail(loginResult.getEmail());
            this.save(userInfo);
        } else {
            String email = loginResult.getEmail();
            // 如果更改了邮箱
            if (StringUtils.hasText(email) && !Objects.equals(email, userInfo.getEmail())) {
                userInfo.setEmail(email);
                this.update(userInfo);
            }
        }
        return userInfo;
    }

    private UserInfo doDatabaseLogin(String username, String password) {
        password = getDbPassword(username, password);
        Query query = new Query()
                .eq("username", username)
                .eq("password", password);
        UserInfo userInfo = get(query);
        Assert.notNull(userInfo, () -> "用户名密码不正确");
        return userInfo;
    }

    private String createToken(long userId) {
        String id = IdUtil.encode(userId);
        Map<String, String> data = new HashMap<>(4);
        data.put("id", String.valueOf(userId));
        String jwt = JwtUtil.createJwt(data, jwtTimeoutDays, jwtSecret);
        return id + ":" + jwt;
    }

    /**
     * 重置用户密码
     *
     * @param id id
     * @return 返回重置后的密码
     */
    public String resetPassword(Long id) {
        if (EnvironmentKeys.LOGIN_THIRD_PARTY_ENABLE.getBoolean()) {
            throw new BizException("已开启第三方登录，不支持重置密码");
        }
        UserInfo userInfo = getById(id);
        String newPwd = PasswordUtil.getRandomSimplePassword(6);
        String password = DigestUtils.md5DigestAsHex(newPwd.getBytes(StandardCharsets.UTF_8));
        password = getDbPassword(userInfo.getUsername(), password);
        userInfo.setPassword(password);
        userInfo.setStatus(UserStatusEnum.SET_PASSWORD.getStatus());
        this.update(userInfo);
        return newPwd;
    }

    /**
     * 禁用用户
     *
     * @param id id
     */
    public void disableUser(Long id) {
        UserInfo userInfo = getById(id);
        userInfo.setStatus(UserStatusEnum.DISABLED.getStatus());
        this.update(userInfo);
    }

    /**
     * 启用用户
     *
     * @param id id
     */
    public void enableUser(Long id) {
        UserInfo userInfo = getById(id);
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        this.update(userInfo);
    }

    public List<UserInfo> listSuperAdmin() {
        return this.list("is_super_admin", Booleans.TRUE);
    }

}