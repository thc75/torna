package cn.torna.service;

import cn.torna.dao.entity.UserWeComInfo;
import cn.torna.dao.mapper.UserWeComInfoMapper;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class UserWeComInfoService extends BaseLambdaService<UserWeComInfo, UserWeComInfoMapper> {

    public UserWeComInfo getByUserId(long userId) {
        return get(UserWeComInfo::getUserInfoId, userId);
    }

}
