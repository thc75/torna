package cn.torna.web.config;

import cn.torna.common.context.EnvironmentContext;
import cn.torna.common.context.SpringContext;
import cn.torna.common.message.MessageFactory;
import cn.torna.common.support.HashIdParamResolver;
import cn.torna.common.thread.TornaAsyncConfigurer;
import cn.torna.common.util.FastjsonUtil;
import cn.torna.common.util.SystemUtil;
import cn.torna.web.controller.doc.DebugController;
import cn.torna.web.interceptor.AdminInterceptor;
import cn.torna.web.interceptor.LoginInterceptor;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @author tanghc
 */
@Configuration
@Slf4j
// 开启异步调用
@EnableAsync
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware, InitializingBean {

    @Value("${torna.front-location:}")
    private String frontLocation;

    @Autowired
    private Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.setApplicationContext(applicationContext);
    }

    /**
     * 配置拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludes = {
                // 排除前端资源
                "/", "/*.html", "/*.ico", "/static/**",
                // 排除服务端请求
                "/api", "/api/**", "/opendoc/**", "/doc/debug/**", "/system/**", "/captcha/**",
                "/mock/**", "/error", "/share/**"
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
    public CorsFilter corsFilter(
            @Value("${torna.cors.allowed-origin-pattern:*}") String allowedOriginPattern,
            @Value("${torna.cors.allowed-header:*}") String allowedHeader
    ) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // SpringBoot升级2.4.0之后，跨域配置中的.allowedOrigins不再可用,改成addAllowedOriginPattern
        corsConfiguration.addAllowedOriginPattern(allowedOriginPattern);
        corsConfiguration.addAllowedHeader(allowedHeader);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.addExposedHeader(DebugController.TARGET_RESPONSE_HEADERS_NAME);
        corsConfiguration.addExposedHeader("Content-Disposition");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }


    /**
     * 配置静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontRoot;
        if (StringUtils.hasText(frontLocation)) {
            frontRoot = StringUtils.trimTrailingCharacter(frontLocation, '/');
        } else {
            String homeDir = SystemUtil.getBinPath();
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
     *
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonConfigure() {
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


    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> messages = Arrays.asList("i18n/message/message");
        MessageFactory.initMessageSource(messages);
        EnvironmentContext.setEnvironment(environment);
    }

}
