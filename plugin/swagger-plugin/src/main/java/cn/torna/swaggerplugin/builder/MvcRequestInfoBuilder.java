package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.PluginUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author tanghc
 */
public class MvcRequestInfoBuilder extends HttpMethodInfoBuilder {

    public MvcRequestInfoBuilder(Method method, TornaConfig tornaConfig) {
        super(method, tornaConfig);
    }

    @Override
    public String buildUrl() {
        Method method = getMethod();
        RequestMapping baseMapping = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequestMapping.class);
        String basePath = "/";
        if (baseMapping != null) {
            String[] value = baseMapping.value();
            if (value.length > 0) {
                basePath = '/' + StringUtils.trimLeadingCharacter(value[0], '/');
            }
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            String[] value = requestMapping.value();
            if (value.length > 0) {
                String path = '/' + StringUtils.trimLeadingCharacter(value[0], '/');
                return basePath + path;
            } else {
                // 如果没有指定方法上的RequestMapping.value，则使用类上面的RequestMapping.value
                return basePath;
            }
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
        String[] consumeArr = getConsumes();
        if (consumeArr != null && consumeArr.length > 0) {
            return consumeArr[0];
        }
        Parameter[] methodParameters = getMethod().getParameters();
        if (methodParameters.length == 0) {
            return "";
        }
        for (Parameter methodParameter : methodParameters) {
            RequestBody requestBody = methodParameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                return MediaType.APPLICATION_JSON_VALUE;
            }
            if (PluginUtil.isFileParameter(methodParameter)) {
                return MediaType.MULTIPART_FORM_DATA_VALUE;
            }
        }
        return getTornaConfig().getGlobalContentType();
    }

    private String[] getConsumes() {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(getMethod(), RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.consumes();
        }
        return null;
    }

}
