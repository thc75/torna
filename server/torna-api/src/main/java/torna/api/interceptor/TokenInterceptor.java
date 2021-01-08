package torna.api.interceptor;

import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;
import com.gitee.easyopen.message.Errors;
import org.springframework.util.StringUtils;
import torna.api.bean.RequestContext;
import torna.common.context.SpringContext;
import torna.dao.entity.Module;
import torna.service.ModuleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
public class TokenInterceptor extends ApiInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        String accessToken = ApiContext.getAccessToken();
        if (StringUtils.isEmpty(accessToken)) {
            throw Errors.ERROR_ACCESS_TOKEN.getException();
        }
        ModuleService moduleService = SpringContext.getBean(ModuleService.class);
        Module module = moduleService.getByToken(accessToken);
        if (module == null) {
            throw Errors.ERROR_ACCESS_TOKEN.getException();
        }
        RequestContext.getCurrentContext().setModuleId(module.getId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        RequestContext.getCurrentContext().reset();
    }
}
