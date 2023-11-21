package cn.torna.service;

import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.UserInfoMapper;
import cn.torna.service.dto.DingTalkLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author thc
 */
@Service
@Slf4j
public class UserInfoProService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserDingtalkInfoService userDingtalkInfoService;

    /**
     * 绑定钉钉账号
     * @param dingTalkLoginDTO 钉钉账号信息
     * @param userId torna用户id
     */
    public void bindDingDingAccount(DingTalkLoginDTO dingTalkLoginDTO, long userId) {
        UserInfo userInfo = userInfoMapper.getById(userId);
        log.info("绑定钉钉用户, 钉钉账号信息={}, Torna 用户Id={}", dingTalkLoginDTO, userId);
        userDingtalkInfoService.addUser(dingTalkLoginDTO, userInfo);
    }

}
