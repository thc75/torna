package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.PluginUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @author tanghc
 * @version 1.0.0
 * @description
 * @date 2021/7/14/014
 */
public class MvcRequestInfoBuilder extends HttpMethodInfoBuilder {

    public MvcRequestInfoBuilder(Method method, TornaConfig tornaConfig) {
        super(method, tornaConfig);
    }

    @Override
    public String buildUrl() {
        Method method = getMethod();
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.value()[0];
        } else {
            return method.toString();
        }
    }

    @Override
    public String buildContentType() {
        String consumes = getApiOperation().consumes();
        if (StringUtils.hasText(consumes)) {
            return consumes;
        }
        Parameter[] methodParameters = getMethod().getParameters();
        for (Parameter methodParameter : methodParameters) {
            RequestBody requestBody = methodParameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                return MediaType.APPLICATION_JSON_VALUE;
            }
            if (PluginUtil.isFileParameter(methodParameter)) {
                return MediaType.MULTIPART_FORM_DATA_VALUE;
            }
        }
        String[] consumeArr = getConsumes();
        if (consumeArr != null && consumeArr.length > 0) {
            return consumeArr[0];
        }
        return getTornaConfig().getGlobalContentType();
    }

    private String[] getConsumes() {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(getMethod(), RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.consumes();
        }
        return null;
    }

}
