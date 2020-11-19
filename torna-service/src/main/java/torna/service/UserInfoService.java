package torna.service;

import torna.common.bean.Booleans;
import torna.common.exception.BizException;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
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

import java.util.Collections;
import java.util.List;
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

    /**
     * 添加新用户，用于注册
     * @param userAddDTO 用户信息
     */
    @Transactional
    public void addUser(UserAddDTO userAddDTO) {
        // 1. 保存用户
        userAddDTO.setIsAdmin(Booleans.FALSE);
        UserInfo userInfo = CopyUtil.copyBean(userAddDTO, UserInfo::new);
        this.saveIgnoreNull(userInfo);

        // 2. 为用户生成一个默认空间
        Long userId = userInfo.getId();
        SpaceAddDTO spaceAddDTO = new SpaceAddDTO();
        spaceAddDTO.setLeaderId(userId);
        spaceAddDTO.setCreatorId(userId);
        spaceAddDTO.setCreator(userInfo.getRealname());
        spaceAddDTO.setName(defaultSpaceName);
        spaceService.addSpace(spaceAddDTO);
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
        List<String> usernames = CopyUtil.copyList(list, UserInfo::getRealname);
        throw new BizException(String.join("、", usernames) + " 已存在");
    }
}