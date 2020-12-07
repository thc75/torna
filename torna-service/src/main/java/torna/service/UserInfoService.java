package torna.service;

import org.springframework.util.Assert;
import torna.common.bean.Booleans;
import torna.common.bean.LoginUser;
import torna.common.exception.BizException;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.common.util.GenerateUtil;
import torna.dao.entity.UserInfo;
import torna.dao.mapper.UserInfoMapper;
import torna.service.dto.SpaceAddDTO;
import torna.service.dto.UserAddDTO;
import torna.service.dto.UserInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Service
public class UserInfoService extends BaseService<UserInfo, UserInfoMapper> {

    @Autowired
    private SpaceService spaceService;

    @Value("${torna.config.default-space-name:默认空间}")
    private String defaultSpaceName;

    @Value("${torna.password.salt:@3dG%gm^uu&=.}")
    private String salt;

    /**
     * 添加新用户，用于注册
     * @param userAddDTO 用户信息
     */
    @Transactional
    public void addUser(UserAddDTO userAddDTO) {
        // 1. 保存用户
        userAddDTO.setIsSuperAdmin(Booleans.FALSE);
        UserInfo userInfo = CopyUtil.copyBean(userAddDTO, UserInfo::new);
        String password = getDbPassword(userAddDTO.getUsername(), userAddDTO.getPassword());
        userInfo.setPassword(password);
        this.saveIgnoreNull(userInfo);

        // 2. 为用户生成一个默认空间，且自己是管理员
        Long userId = userInfo.getId();
        SpaceAddDTO spaceAddDTO = new SpaceAddDTO();
        spaceAddDTO.setAdminIds(Collections.singletonList(userId));
        spaceAddDTO.setCreatorId(userId);
        spaceAddDTO.setCreatorName(userInfo.getNickname());
        spaceAddDTO.setName(defaultSpaceName);
        spaceService.addSpace(spaceAddDTO);
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

    public LoginUser getLoginUser(String username, String password) {
        Assert.notNull(username, () -> "用户名不能为空");
        Assert.notNull(password, () -> "密码不能为空");
        Query query = new Query()
                .eq("username", username)
                .eq("password", password);
        UserInfo userInfo = get(query);
        Assert.notNull(userInfo, () -> "用户名密码不正确");
        return CopyUtil.copyBean(userInfo, LoginUser::new);
    }
}