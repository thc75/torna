package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

/**
 * @author tanghc
 */
public abstract class HttpMethodInfoBuilder implements RequestInfoBuilder {

    private TornaConfig tornaConfig;
    private Method method;
    private ApiOperation apiOperation;

    public HttpMethodInfoBuilder(Method method, TornaConfig tornaConfig) {
        this.tornaConfig = tornaConfig;
        this.method = method;
        this.apiOperation = method.getAnnotation(ApiOperation.class);
    }

    @Override
    public String getHttpMethod() {
        String httpMethod = apiOperation.httpMethod();
        if (StringUtils.hasText(httpMethod)) {
            return httpMethod;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            RequestMethod[] methods = requestMapping.method();
            if (methods.length == 0) {
                return this.tornaConfig.getMethodWhenMulti();
            } else {
                return methods[0].name();
            }
        }
        return tornaConfig.getDefaultHttpMethod();
    }

    public TornaConfig getTornaConfig() {
        return tornaConfig;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public ApiOperation getApiOperation() {
        return apiOperation;
    }
}
