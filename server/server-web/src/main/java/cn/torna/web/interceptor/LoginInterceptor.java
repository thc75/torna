package cn.torna.web.interceptor;

import cn.torna.common.context.UserContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.exception.LoginFailureException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
