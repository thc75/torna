package torna.web.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import torna.common.context.SpringContext;
import torna.common.support.HashIdParamResolver;
import torna.common.util.FastjsonUtil;
import torna.web.interceptor.AdminInterceptor;
import torna.web.interceptor.LoginInterceptor;

import java.util.List;

/**
 * @author tanghc
 */
@Configuration
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.setApplicationContext(applicationContext);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludes = { "/api", "/api/**", "/opendoc/**" };
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(excludes)
                // 排除调试请求
                .excludePathPatterns("/doc/debug");
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin")
                .excludePathPatterns(excludes);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HashIdParamResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("target-response-headers");
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


}
