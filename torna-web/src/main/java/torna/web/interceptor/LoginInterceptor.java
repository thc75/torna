package torna.web.interceptor;

import torna.common.annotation.NoLogin;
import torna.common.context.UserContext;
import torna.common.exception.LoginFailureException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getServletPath().startsWith("/open")) {
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
}
