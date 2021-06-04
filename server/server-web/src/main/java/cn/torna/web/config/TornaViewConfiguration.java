package cn.torna.web.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tanghc
 */
@Configuration
@EnableConfigurationProperties(TornaViewProperties.class)
public class TornaViewConfiguration {
}
