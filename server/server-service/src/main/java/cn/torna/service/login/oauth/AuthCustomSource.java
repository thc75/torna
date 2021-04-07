package cn.torna.service.login.oauth;

import cn.torna.common.context.EnvironmentContext;
import me.zhyd.oauth.config.AuthSource;

/**
 * @author tanghc
 */
public enum AuthCustomSource implements AuthSource {

    CUSTOM {
        @Override
        public String authorize() {
            return EnvironmentContext.getValue("torna.login.third-party.oauth.url.authorize");
        }

        @Override
        public String accessToken() {
            return EnvironmentContext.getValue("torna.login.third-party.oauth.url.access-token");
        }

        @Override
        public String userInfo() {
            return EnvironmentContext.getValue("torna.login.third-party.oauth.url.user-info");
        }
    }

}
