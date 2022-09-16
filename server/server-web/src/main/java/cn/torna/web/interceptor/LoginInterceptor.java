package cn.torna.web.interceptor;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.UserStatusEnum;
import cn.torna.common.exception.LoginFailureException;
import cn.torna.common.exception.SetPasswordException;
import cn.torna.common.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
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
        User user = UserContext.getUser();
        if (user == null || UserStatusEnum.of(user.getStatus()) == UserStatusEnum.DISABLED) {
            String token = UserContext.getToken(request);
            String userId = UserContext.getPrefixUserId(token);
            log.error("登录失败，userId:{}, 客户端ip:{}, uri:{}", userId, RequestUtil.getIP(request), request.getRequestURI());
            throw new LoginFailureException("登录失败");
        }
        if (UserStatusEnum.of(user.getStatus()) == UserStatusEnum.SET_PASSWORD) {
            throw new SetPasswordException();
        }
        return true;
    }

}
