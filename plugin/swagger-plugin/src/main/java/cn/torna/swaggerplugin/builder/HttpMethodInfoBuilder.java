package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            RequestMethod[] methods = requestMapping.method();
            if (methods.length == 0) {
                return this.tornaConfig.getMethodWhenMulti();
            } else {
                return methods[0].name();
            }
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) return HttpMethod.GET.name();
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) return HttpMethod.POST.name();
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) return HttpMethod.PUT.name();
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) return HttpMethod.DELETE.name();
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) return HttpMethod.PATCH.name();
        return HttpMethod.GET.name();
    }

    public TornaConfig getTornaConfig() {
        return tornaConfig;
    }

    public Method getMethod() {
        return method;
    }

    public ApiOperation getApiOperation() {
        return apiOperation;
    }
}
