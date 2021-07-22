package cn.torna.swaggerplugin.starter;

import cn.torna.swaggerplugin.SwaggerPluginConfiguration;
import cn.torna.swaggerplugin.bean.TornaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;

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
    public SwaggerPluginConfiguration swaggerPluginConfiguration(BeanFactory beanFactory) {
        TornaConfig tornaConfig = new TornaConfig();
        BeanUtils.copyProperties(tornaProperties, tornaConfig);
        this.loadPackage(tornaConfig, beanFactory);
        return new SwaggerPluginConfiguration(tornaConfig);
    }

    private void loadPackage(TornaConfig tornaConfig, BeanFactory beanFactory) {
        if (StringUtils.hasLength(tornaConfig.getBasePackage())) {
            return;
        }
        Collection<String> packages = AutoConfigurationPackages.get(beanFactory);
        String basePackage = String.join(";", packages);
        tornaConfig.setBasePackage(basePackage);
    }

}
