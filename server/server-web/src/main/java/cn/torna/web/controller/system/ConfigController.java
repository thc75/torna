package cn.torna.web.controller.system;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.Result;
import cn.torna.common.context.EnvironmentContext;
import cn.torna.common.enums.ThirdPartyLoginTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.web.config.TornaViewProperties;
import cn.torna.web.controller.system.vo.ConfigVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
public class ConfigController implements InitializingBean {

    private ConfigVO configVO = new ConfigVO();

    private String oauthLoginUrl = "/system/oauth/login/custom";

    @Value("${torna.login.third-party.oauth.login-button-text:第三方登录}")
    private String oauthButtonText;

    @Autowired
    private TornaViewProperties tornaViewProperties;

    @GetMapping("/config")
    public Result<ConfigVO> config() {
        return Result.ok(configVO);
    }

    @GetMapping("/viewConfig")
    public Result<TornaViewProperties> viewConfig() {
        return Result.ok(tornaViewProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean enableThirdPartyLogin = EnvironmentKeys.LOGIN_THIRD_PARTY_ENABLE.getBoolean();
        boolean enableReg = EnvironmentKeys.REGISTER_ENABLE.getBoolean() && !enableThirdPartyLogin;
        boolean typeForm = false;
        boolean typeOauth = false;
        if (enableThirdPartyLogin) {
            String value = EnvironmentKeys.LOGIN_THIRD_PARTY_TYPE.getValue();
            typeForm = Objects.equals(value, ThirdPartyLoginTypeEnum.FORM.getType());
            typeOauth = Objects.equals(value, ThirdPartyLoginTypeEnum.OAUTH.getType());
        }
        configVO.setEnableThirdPartyLogin(enableThirdPartyLogin);
        configVO.setEnableReg(enableReg);
        configVO.setEnableThirdPartyForm(typeForm);
        configVO.setEnableThirdPartyOauth(typeOauth);
        configVO.setOauthLoginUrl(oauthLoginUrl);
        configVO.setOauthButtonText(oauthButtonText);
        configVO.setIgnoreParam(Boolean.parseBoolean(EnvironmentContext.getValue("torna.mock.ignore-param", "false")));

        CopyUtil.copyProperties(configVO, tornaViewProperties);
    }
}
