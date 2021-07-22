package cn.torna.swaggerplugin;

import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.PluginUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void init() throws IOException {
        if (this.tornaConfig == null) {
            this.tornaConfig = buildTornaConfig();
        }
        this.loadConfig(tornaConfig);
        this.loadBasePackage(tornaConfig);
        new SwaggerPluginService(tornaConfig).pushDoc();
    }

    protected TornaConfig buildTornaConfig() {
        TornaConfig tornaConfig = new TornaConfig();
        tornaConfig.setConfigFile(environment.getProperty("torna.swagger-plugin.config-file", "torna.json"));
        tornaConfig.setEnable(Boolean.parseBoolean(environment.getProperty("torna.swagger-plugin.enable", "false")));
        tornaConfig.setBasePackage(environment.getProperty("torna.swagger-plugin.basePackage", ""));
        tornaConfig.setUrl(environment.getProperty("torna.swagger-plugin.url"));
        tornaConfig.setAppKey(environment.getProperty("torna.swagger-plugin.app-key"));
        tornaConfig.setSecret(environment.getProperty("torna.swagger-plugin.secret"));
        tornaConfig.setToken(environment.getProperty("torna.swagger-plugin.token"));
        tornaConfig.setDebugEnv(environment.getProperty("torna.swagger-plugin.debug-env"));
        tornaConfig.setAuthor(environment.getProperty("torna.swagger-plugin.author"));
        tornaConfig.setDebug(Boolean.parseBoolean(environment.getProperty("torna.swagger-plugin.debug", "false")));
        tornaConfig.setMethodWhenMulti(environment.getProperty("torna.swagger-plugin.method-when-multi", "GET"));
        tornaConfig.setHasBodyMethods(environment.getProperty("torna.swagger-plugin.has-body-methods", "POST,PUT,DELETE"));
        tornaConfig.setIsReplace(Boolean.parseBoolean(environment.getProperty("torna.swagger-plugin.is-replace", "true")));
        return tornaConfig;
    }

    protected void loadConfig(TornaConfig tornaConfig) throws IOException {
        String configFile = environment.getProperty("torna.push.configfile", tornaConfig.getConfigFile());
        ClassPathResource classPathResource = new ClassPathResource(configFile);
        if (classPathResource.exists()) {
            System.out.println("加载Torna配置文件:" + configFile);
            InputStream inputStream = classPathResource.getInputStream();
            String json = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            TornaConfig tornaConfigOther = jsonObject.toJavaObject(TornaConfig.class);
            PluginUtil.copyPropertiesIgnoreNull(tornaConfigOther, tornaConfig);
            tornaConfig.setJarClass(jsonObject.getJSONObject("jarClass"));
        }
    }

    protected void loadBasePackage(TornaConfig tornaConfig) {
        if (StringUtils.hasLength(tornaConfig.getBasePackage())) {
            return;
        }
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> packages = handlerMethods.values()
                .stream()
                .map(handlerMethod -> {
                    Package aPackage = handlerMethod.getBeanType().getPackage();
                    return aPackage.getName();
                })
                .collect(Collectors.toSet());
        String basePackage = String.join(";", packages);
        tornaConfig.setBasePackage(basePackage);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            init();
        } catch (Exception e) {
            System.out.println("推送Torna文档失败，msg:" + e.getMessage());
            e.printStackTrace();
        }
    }



}
