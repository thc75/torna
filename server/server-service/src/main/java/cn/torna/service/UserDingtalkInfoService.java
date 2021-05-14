package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.UserDingtalkInfoMapper;
import cn.torna.service.dto.DingTalkLoginDTO;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class UserDingtalkInfoService extends BaseService<UserDingtalkInfo, UserDingtalkInfoMapper> {

    public void addUser(DingTalkLoginDTO dingTalkLoginDTO, UserInfo userInfo) {
        UserDingtalkInfo userDingtalkInfo = getByUnionId(dingTalkLoginDTO.getUnionid());
        boolean save = false;
        if (userDingtalkInfo == null) {
            userDingtalkInfo = new UserDingtalkInfo();
            save = true;
        }
        userDingtalkInfo.setUserid(dingTalkLoginDTO.getUserid());
        userDingtalkInfo.setNick(dingTalkLoginDTO.getNick());
        userDingtalkInfo.setName(dingTalkLoginDTO.getName());
        userDingtalkInfo.setEmail(dingTalkLoginDTO.getEmail());
        userDingtalkInfo.setUnionid(dingTalkLoginDTO.getUnionid());
        userDingtalkInfo.setOpenid(dingTalkLoginDTO.getOpenid());
        userDingtalkInfo.setUserInfoId(userInfo.getId());
        if (save) {
            save(userDingtalkInfo);
        } else {
            update(userDingtalkInfo);
        }
    }

    public UserDingtalkInfo getByUnionId(String unionId) {
        return get("unionid", unionId);
    }
}