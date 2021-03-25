package cn.torna.web.config;

import cn.torna.common.context.SpringContext;
import cn.torna.common.message.MessageFactory;
import cn.torna.common.support.HashIdParamResolver;
import cn.torna.common.thread.TornaAsyncConfigurer;
import cn.torna.common.util.FastjsonUtil;
import cn.torna.web.interceptor.AdminInterceptor;
import cn.torna.web.interceptor.LoginInterceptor;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanghc
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

    @Value("${torna.front-location:}")
    private String frontLocation;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.setApplicationContext(applicationContext);
    }

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludes = {
                // 排除前端资源
                "/", "/*.html", "/*.ico", "/static/**",
                // 排除服务端请求
                "/api", "/api/**", "/opendoc/**", "/doc/debug/**", "/system/**", "/captcha/**",
                "/mock/**"
        };
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(excludes);
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(excludes);
    }

    /**
     * 跨域设置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("target-response-headers");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }


    /**
     * 配置静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontRoot;
        if (StringUtils.hasText(frontLocation)) {
            frontRoot = StringUtils.trimTrailingCharacter(frontLocation, '/');
        } else {
            ApplicationHome applicationHome = new ApplicationHome(getClass());
            File file = applicationHome.getSource();
            String homeDir = file.getParentFile().toString();
            frontRoot = homeDir + "/dist";
        }
        log.info("前端资源目录：{}", frontRoot);
        String frontLocation = "file:" + frontRoot;
        registry.addResourceHandler("/index.html").addResourceLocations(frontLocation + "/index.html");
        registry.addResourceHandler("/favicon.ico").addResourceLocations(frontLocation + "/favicon.ico");
        registry.addResourceHandler("/static/**").addResourceLocations(frontLocation + "/static/");
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HashIdParamResolver());
    }


    /**
     * 使用fastjson代替jackson
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonConfigure(){
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(FastjsonUtil.SERIALIZER_FEATURES);
        // 日期格式化
        fastJsonConfig.setDateFormat(FastjsonUtil.DATE_FORMAT);
        converter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(converter);
    }

    @Bean
    public TornaAsyncConfigurer asyncConfigurer(@Value("${torna.thread-pool-size:4}") int threadPoolSize) {
        return new TornaAsyncConfigurer("torna-sync", threadPoolSize);
    }

    @PostConstruct
    public void after() {
        List<String> messages = Arrays.asList("i18n/message/message");
        MessageFactory.initMessageSource(messages);
    }

}
