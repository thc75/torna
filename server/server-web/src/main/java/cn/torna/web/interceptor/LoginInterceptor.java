package cn.torna.web.interceptor;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.LoginFailureException;
import cn.torna.common.exception.SetPasswordException;
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
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NoLogin noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
        if (noLogin != null) {
            return true;
        }
        noLogin = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), NoLogin.class);
        if (noLogin != null) {
            return true;
        }
        User user = UserContext.getUser();
        if (user == null || UserStatusEnum.of(user.getStatus()) == UserStatusEnum.DISABLED) {
            throw new LoginFailureException("登录失败，uri:" + request.getRequestURI());
        }
        if (UserStatusEnum.of(user.getStatus()) == UserStatusEnum.SET_PASSWORD) {
            throw new SetPasswordException();
        }
        return true;
    }

}
