package cn.torna.service;

import cn.torna.common.bean.LoginUser;
import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.*;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.UserInfoMapper;
import cn.torna.service.dto.UserAddDTO;
import cn.torna.service.dto.UserInfoDTO;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
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
     * 是否开启第三方登录
     */
    @Value("${third.login.enable:false}")
    private Boolean thirdLoginEnable;

    /**
     * 第三方登录接口地址
     */
    @Value("${third.login.url:localhost}")
    private String thirdLoginUrl;

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 添加新用户，用于注册
     * @param userAddDTO 用户信息
     */
    public void addUser(UserAddDTO userAddDTO) {
        Assert.isTrue(thirdLoginEnable,"已开启第三方登录，不支持添加新用户");
        // 校验邮箱是否存在
        checkEmail(userAddDTO.getUsername());
        UserInfo userInfo = CopyUtil.copyBean(userAddDTO, UserInfo::new);
        String password = getDbPassword(userAddDTO.getUsername(), userAddDTO.getPassword());
        userInfo.setPassword(password);
        this.save(userInfo);
    }

    public void checkEmail(String email) {
        UserInfo userInfo = get("username", email);
        Assert.isNull(userInfo, () -> "账号已被注册");
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

        UserInfo userInfo = null;

        //是否第三方登录
        if(thirdLoginEnable){
            Assert.isTrue(thirdLoginCheck(username, password),"第三方登录校验不通过");

            Query query = new Query()
                    .eq("username", username);
            userInfo = get(query);

            //用户第一次登录则插入到user_info表
            if(userInfo==null){
                UserInfo newer = new UserInfo();
                newer.setUsername(username);
                newer.setPassword(UUID.randomUUID().toString().replace("-",""));
                newer.setNickname(username);
                newer.setIsSuperAdmin((byte) 1);
                newer.setStatus((byte) 1);
                newer.setIsDeleted((byte) 0);

                this.save(newer);

                userInfo = CopyUtil.copyBean(newer, UserInfo::new);
            }
        }else{
            password = getDbPassword(username, password);
            Query query = new Query()
                    .eq("username", username)
                    .eq("password", password);
            userInfo = get(query);
            Assert.notNull(userInfo, () -> "用户名密码不正确");
        }


        if (UserStatusEnum.of(userInfo.getStatus()) == UserStatusEnum.DISABLED) {
            throw new BizException("用户已禁用，请联系管理员");
        }
        // 登录成功
        LoginUser loginUser = CopyUtil.copyBean(userInfo, LoginUser::new);
        // 创建token
        String token = this.createToken(userInfo.getId());
        loginUser.setToken(token);
        userCacheManager.saveUser(loginUser);
        return loginUser;
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
     * @param id id
     * @return 返回重置后的密码
     */
    public String resetPassword(Long id) {
        Assert.isTrue(thirdLoginEnable,"已开启第三方登录，不支持重置密码");
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
     * @param id id
     */
    public void disableUser(Long id) {
        UserInfo userInfo = getById(id);
        userInfo.setStatus(UserStatusEnum.DISABLED.getStatus());
        this.update(userInfo);
    }

    /**
     * 启用用户
     * @param id id
     */
    public void enableUser(Long id) {
        UserInfo userInfo = getById(id);
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        this.update(userInfo);
    }

    /**
     * 第三方登录账号校验
     * 默认仅支持post请求方式:
     * 入参 { "username":"xxx", "password":"123456"}
     * 返回 { "status":"success" }， status=success时校验通过
     * 其他校验方式需重载此方法
     * @param username 用户名（原文）
     * @param password 密码（原文）
     * @return 校验是否通过
     */
    private Boolean thirdLoginCheck(String username, String password) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HashMap<String, String> params= new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        HttpEntity requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(thirdLoginUrl, requestEntity , String.class );

        if(!StringUtils.isEmpty(response.getBody())){
            JSONObject jsonObject = FastjsonUtil.toJsonObj(response.getBody());

            return StringUtils.isEmpty(jsonObject.getString("status"))?false:jsonObject.getString("status").equals("success");
        }

        return false;
    }

}