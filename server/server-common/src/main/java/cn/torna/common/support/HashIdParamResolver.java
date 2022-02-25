package cn.torna.common.support;

import cn.torna.common.annotation.HashId;
import cn.torna.common.util.IdUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class HashIdParamResolver implements HandlerMethodArgumentResolver {

    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap<>(256);


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(HashId.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String value = nativeWebRequest.getParameter(methodParameter.getParameterName());
        return IdUtil.decode(value);
    }
}
