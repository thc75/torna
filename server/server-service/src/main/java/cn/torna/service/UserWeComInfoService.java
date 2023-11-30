package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.entity.UserWeComInfo;
import cn.torna.dao.mapper.UserDingtalkInfoMapper;
import cn.torna.dao.mapper.UserWeComInfoMapper;
import cn.torna.service.dto.DingTalkLoginDTO;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class UserWeComInfoService extends BaseService<UserWeComInfo, UserWeComInfoMapper> {

    public UserWeComInfo getByUserId(long userId) {
        return get("user_info_id", userId);
    }

}