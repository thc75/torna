package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.common.util.AppKeyUtil;
import cn.torna.common.util.PasswordUtil;
import cn.torna.dao.entity.OpenUser;
import cn.torna.dao.mapper.OpenUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class OpenUserService extends BaseService<OpenUser, OpenUserMapper> {

    private static final int SECRET_LEN = 32;

    public OpenUser getByAppKey(String appKey) {
        if (appKey == null) {
            return null;
        }
        return this.get("app_key", appKey);
    }

    public void createOpenUser(long spaceId, String applicant) {
        OpenUser openUser = new OpenUser();
        openUser.setAppKey(AppKeyUtil.createAppKey());
        openUser.setSecret(createSecret());
        openUser.setSpaceId(spaceId);
        openUser.setApplicant(applicant);
        this.save(openUser);
    }

    public static String createSecret() {
        return PasswordUtil.getRandomPassword(SECRET_LEN);
    }

}