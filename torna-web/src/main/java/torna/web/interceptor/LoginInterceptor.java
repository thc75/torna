package torna.web.interceptor;

import torna.common.annotation.NoLogin;
import torna.common.context.UserContext;
import torna.common.exception.LoginFailureException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanghc
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static List<String> whiteList = Arrays.asList("/api");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 开放接口不检查
        if (isWhiteUrl(request)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NoLogin noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
        if (noLogin != null) {
            return true;
        }
        noLogin = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), NoLogin.class);
        if (noLogin != null) {
            return true;
        }
        if (UserContext.getUser() == null) {
            throw new LoginFailureException();
        }
        return true;
    }

    private static boolean isWhiteUrl(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if ("/".equals(servletPath)) {
            return true;
        }
        for (String uri : whiteList) {
            if (servletPath.startsWith(uri)) {
                return true;
            }
        }
        return false;
    }
}
