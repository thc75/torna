package cn.torna.service;

import cn.torna.common.util.GenerateUtil;
import cn.torna.dao.entity.SystemLoginToken;
import cn.torna.dao.mapper.SystemLoginTokenMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author tanghc
 */
@Service
public class SystemLoginTokenService extends BaseLambdaService<SystemLoginToken, SystemLoginTokenMapper> {


    public Optional<String> getTokenByLoginKey(String loginKey) {
        SystemLoginToken systemLoginToken = get(SystemLoginToken::getLoginKey, loginKey);
        return Optional.ofNullable(systemLoginToken)
                .filter(data -> data.getExpireTime() != null && data.getExpireTime().isAfter(LocalDateTime.now()))
                .map(SystemLoginToken::getToken);
    }

    public String createLoginKey(String token) {
        SystemLoginToken systemLoginToken = new SystemLoginToken();
        String loginKey = GenerateUtil.getUUID();
        systemLoginToken.setLoginKey(loginKey);
        systemLoginToken.setToken(token);
        systemLoginToken.setExpireTime(LocalDateTime.now().plusSeconds(10));
        save(systemLoginToken);
        return loginKey;
    }

    // 每月1号0点10分删除过期数据
    @Scheduled(cron = "0 10 0 1 * ?")
    public void del() {
        Query query = this.query()
                .lt(SystemLoginToken::getExpireTime, new Date());
        this.forceDeleteByQuery(query);
    }

}
