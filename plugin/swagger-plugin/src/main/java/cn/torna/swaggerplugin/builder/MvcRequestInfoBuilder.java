package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.PluginUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @author tanghc
 */
public class MvcRequestInfoBuilder extends HttpMethodInfoBuilder {

    private RequestMappingInfo requestMappingInfo;

    public MvcRequestInfoBuilder(RequestMappingInfo requestMappingInfo, Method method, TornaConfig tornaConfig) {
        super(method, tornaConfig);
        this.requestMappingInfo = requestMappingInfo;
    }

    @Override
    public String buildUrl() {
        Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        return patterns.iterator().next();
    }

    @Override
    public String buildContentType() {
        String consumes = getApiOperation().consumes();
        if (StringUtils.hasText(consumes)) {
            return consumes;
        }
        Parameter[] methodParameters = getMethod().getParameters();
        Set<MediaType> mediaTypes = requestMappingInfo.getConsumesCondition().getConsumableMediaTypes();
        for (Parameter methodParameter : methodParameters) {
            RequestBody requestBody = methodParameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                return MediaType.APPLICATION_JSON_VALUE;
            }
            if (PluginUtil.isFileParameter(methodParameter)) {
                return MediaType.MULTIPART_FORM_DATA_VALUE;
            }
        }
        if (mediaTypes.contains(MediaType.MULTIPART_FORM_DATA)) {
            return MediaType.MULTIPART_FORM_DATA_VALUE;
        }
        if (mediaTypes.contains(MediaType.APPLICATION_FORM_URLENCODED) || getHttpMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            return MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        }
        return "";
    }

}
