package torna.common.context;

import torna.common.bean.TokenManager;
import torna.common.bean.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

/**
 * @author tanghc
 */
public class UserContext {

    public static final String HEADER_TOKEN = "token";

    private static Supplier<String> tokenGetter = () -> {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return getToken(request);
    };

    public static void setTokenGetter(Supplier<String> tokenGetter) {
        UserContext.tokenGetter = tokenGetter;
    }

    /**
     * 获取当前登录用户
     * @return 返回当前登录用户，没有返回null
     */
    public static User getUser() {
        String token = tokenGetter.get();
        return getUser(token);
    }

    private static String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_TOKEN);
    }

    private static User getUser(String token) {
        return SpringContext.getBean(TokenManager.class).getUser(token);
    }

}
