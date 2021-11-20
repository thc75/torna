package cn.torna.api.manager;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.bean.RequestContext;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.OpenUser;
import cn.torna.service.OpenUserService;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiParam;
import com.gitee.easyopen.AppSecretManager;
import com.gitee.easyopen.ParamNames;
import com.gitee.easyopen.message.Errors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author tanghc
 */
public class ApiAppSecretManager implements AppSecretManager {

    @Autowired
    private OpenUserService openUserService;

    @Override
    public void addAppSecret(Map<String, String> appSecretStore) {

    }

    @Override
    public String getSecret(String appKey) {
        OpenUser openUser = openUserService.getByAppKey(appKey);
        if (!(openUser != null && openUser.getStatus() == StatusEnum.ENABLE.getStatus())) {
            ApiParam param = ApiContext.getApiParam();
            throw Errors.ERROR_APP_ID.getException(param.fatchNameVersion(), ParamNames.APP_KEY_NAME);
        }
        this.saveUser(openUser);
        return openUser.getSecret();
    }

    private void saveUser(OpenUser openUser) {
        ApiUser apiUser = CopyUtil.copyBean(openUser, ApiUser::new);
        RequestContext.getCurrentContext().setApiUser(apiUser);
    }

    @Override
    public boolean isValidAppKey(String appKey) {
        return true;
    }
}
