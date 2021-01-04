package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import torna.common.bean.Booleans;
import torna.common.bean.LoginUser;
import torna.common.bean.User;
import torna.common.bean.UserCacheManager;
import torna.common.exception.BizException;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.common.util.GenerateUtil;
import torna.common.util.IdUtil;
import torna.common.util.JwtUtil;
import torna.common.util.PasswordUtil;
import torna.dao.entity.UserInfo;
import torna.dao.mapper.UserInfoMapper;
import torna.service.dto.UserAddDTO;
import torna.service.dto.UserInfoDTO;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Service
public class UserInfoService extends BaseService<UserInfo, UserInfoMapper> {

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${torna.password.salt:@3dG%gm^uu&=.}")
    private String salt;

    @Value("${torna.jwt.timeout-days:365}")
    private int jwtTimeoutDays;

    @Value("${torna.jwt.secret:CHezCvjte^WHy5^#MqSVx9A%6.F$eV}")
    private String jwtSecret;

    /**
     * 添加新用户，用于注册
     * @param userAddDTO 用户信息
     */
    public void addUser(UserAddDTO userAddDTO) {
        userAddDTO.setIsSuperAdmin(Booleans.FALSE);
        UserInfo userInfo = CopyUtil.copyBean(userAddDTO, UserInfo::new);
        String password = getDbPassword(userAddDTO.getUsername(), userAddDTO.getPassword());
        userInfo.setPassword(password);
        this.save(userInfo);
    }

    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            this.getMapper().logout(token);
        }
    }

    public User getLoginUser(long id) {
        UserInfo userInfo = getById(id);
        return CopyUtil.copyBean(userInfo, LoginUser::new);
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

    public LoginUser login(String username, String password) {
        Assert.notNull(username, () -> "用户名不能为空");
        Assert.notNull(password, () -> "密码不能为空");
        Query query = new Query()
                .eq("username", username)
                .eq("password", password);
        UserInfo userInfo = get(query);
        Assert.notNull(userInfo, () -> "用户名密码不正确");
        // 登录成功
        LoginUser loginUser = CopyUtil.copyBean(userInfo, LoginUser::new);
        // 创建token
        String token = this.createToken(loginUser);
        loginUser.setToken(token);
        userCacheManager.saveUser(loginUser);
        // jwt保存到数据库
        this.getMapper().updateToken(userInfo.getId(), token);
        return loginUser;
    }

    private String createToken(User user) {
        String id = IdUtil.encode(user.getUserId());
        Map<String, String> data = new HashMap<>(4);
        data.put("id", user.getUserId().toString());
        String jwt = JwtUtil.createJwt(data, jwtTimeoutDays, jwtSecret);
        return id + ":" + jwt;
    }

    /**
     * 重置用户密码
     * @param id id
     * @return 返回重置后的密码
     */
    public String resetPassword(Long id) {
        UserInfo userInfo = getById(id);
        String newPwd = PasswordUtil.getRandomSimplePassword(6);
        String password = DigestUtils.md5DigestAsHex(newPwd.getBytes(StandardCharsets.UTF_8));
        password = getDbPassword(userInfo.getUsername(), password);
        userInfo.setPassword(password);
        this.update(userInfo);
        return newPwd;
    }
}