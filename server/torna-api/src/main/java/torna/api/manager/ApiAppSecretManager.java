package torna.api.manager;

import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiParam;
import com.gitee.easyopen.AppSecretManager;
import com.gitee.easyopen.ParamNames;
import com.gitee.easyopen.message.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import torna.api.bean.ApiUser;
import torna.api.bean.RequestContext;
import torna.common.enums.StatusEnum;
import torna.common.util.CopyUtil;
import torna.dao.entity.OpenUser;
import torna.service.OpenUserService;

import java.util.Map;

/**
 * @author tanghc
 */
@Service
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
