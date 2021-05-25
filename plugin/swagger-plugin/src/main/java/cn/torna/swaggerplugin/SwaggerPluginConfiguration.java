package cn.torna.swaggerplugin;

import cn.torna.swaggerplugin.bean.TornaConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author tanghc
 */
public class SwaggerPluginConfiguration implements InitializingBean {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private Environment environment;

    private TornaConfig tornaConfig;

    public SwaggerPluginConfiguration(TornaConfig tornaConfig) {
        this.tornaConfig = tornaConfig;
    }

    public SwaggerPluginConfiguration() {
    }

    public void init(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        if (this.tornaConfig == null) {
            this.tornaConfig = buildTornaConfig();
        }
        new SwaggerPluginService(requestMappingHandlerMapping, tornaConfig).init();
    }

    protected TornaConfig buildTornaConfig() {
        TornaConfig tornaConfig = new TornaConfig();
        tornaConfig.setEnable(environment.getProperty("torna.swagger-plugin.enable"));
        tornaConfig.setBasePackage(environment.getProperty("torna.swagger-plugin.basePackage", ""));
        tornaConfig.setUrl(environment.getProperty("torna.swagger-plugin.url"));
        tornaConfig.setAppKey(environment.getProperty("torna.swagger-plugin.app-key"));
        tornaConfig.setSecret(environment.getProperty("torna.swagger-plugin.secret"));
        tornaConfig.setToken(environment.getProperty("torna.swagger-plugin.token"));
        tornaConfig.setDebugEnv(environment.getProperty("torna.swagger-plugin.debug-env"));
        tornaConfig.setAuthor(environment.getProperty("torna.swagger-plugin.author"));
        tornaConfig.setDebug(environment.getProperty("torna.swagger-plugin.debug"));
        tornaConfig.setMethodWhenMulti(environment.getProperty("torna.swagger-plugin.method-when-multi", "GET"));
        tornaConfig.setPathName(environment.getProperty("torna.swagger-plugin.path-name", "path"));
        tornaConfig.setHeaderName(environment.getProperty("torna.swagger-plugin.header-name", "header"));
        tornaConfig.setQueryName(environment.getProperty("torna.swagger-plugin.query-name", "query"));
        tornaConfig.setFormName(environment.getProperty("torna.swagger-plugin.form-name", "form"));
        tornaConfig.setBodyName(environment.getProperty("torna.swagger-plugin.body-name", "body"));
        return tornaConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            init(requestMappingHandlerMapping);
        } catch (Exception e) {
            System.out.println("推送Torna文档失败，msg:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
