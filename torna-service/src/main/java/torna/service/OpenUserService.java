package torna.service;

import torna.common.support.BaseService;
import torna.common.util.AppKeyUtil;
import torna.common.util.PasswordUtil;
import torna.dao.entity.OpenUser;
import torna.dao.mapper.OpenUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void createOpenUser() {
        OpenUser openUser = new OpenUser();
        openUser.setAppKey(AppKeyUtil.createAppKey());
        openUser.setSecret(createSecret());
        this.saveIgnoreNull(openUser);
    }

    public static String createSecret() {
        return PasswordUtil.getRandomPassword(SECRET_LEN);
    }

}