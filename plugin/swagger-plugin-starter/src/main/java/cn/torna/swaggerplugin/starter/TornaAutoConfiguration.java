package cn.torna.swaggerplugin.starter;

import cn.torna.swaggerplugin.SwaggerPluginConfiguration;
import cn.torna.swaggerplugin.bean.TornaConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author tanghc
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(TornaProperties.class)
public class TornaAutoConfiguration {

    private final TornaProperties tornaProperties;

    public TornaAutoConfiguration(TornaProperties tornaProperties) {
        this.tornaProperties = tornaProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = TornaProperties.PREFIX, name = "enable", havingValue = "true")
    public SwaggerPluginConfiguration swaggerPluginConfiguration() {
        TornaConfig tornaConfig = new TornaConfig();
        BeanUtils.copyProperties(tornaProperties, tornaConfig);
        return new SwaggerPluginConfiguration(tornaConfig);
    }

}
