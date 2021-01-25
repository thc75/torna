package cn.torna.web.interceptor;

import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import cn.torna.common.exception.BizException;
import cn.torna.common.exception.LoginFailureException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = UserContext.getUser();
        if (user == null) {
            throw new LoginFailureException();
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        throw new BizException("无权访问");
    }
}
