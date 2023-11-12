package cn.torna.web.interceptor;

import cn.torna.common.bean.User;
import cn.torna.web.config.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.exception.LoginFailureException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        User user = UserContext.getUser(request);
        if (user == null) {
            throw new LoginFailureException();
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        throw new BizException("无权访问");
    }
}
