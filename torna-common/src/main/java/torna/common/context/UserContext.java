package torna.common.context;

import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import torna.common.bean.TokenManager;
import torna.common.bean.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import torna.common.bean.UserCacheManager;
import torna.common.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author tanghc
 */
public class UserContext {

    public static final String HEADER_TOKEN = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    private static final String SECRET_KEY = "torna.jwt.secret";


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

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_TOKEN);
        if (StringUtils.hasText(token) && token.startsWith(JWT_PREFIX)) {
            return token.substring(JWT_PREFIX.length());
        }
        return token;
    }

    /**
     * 获取登录用户
     * @param token
     * @return
     */
    private static User getUser(String token) {
        if (StringUtils.isEmpty(token) || !token.contains(":")) {
            return null;
        }
        String jwt = token.split(":")[1];
        String secret = SpringContext.getBean(Environment.class).getProperty(SECRET_KEY);
        Map<String, Claim> data = JwtUtil.verifyJwt(jwt, secret);
        Claim id = data.get("id");
        if (id == null) {
            return null;
        }
        long userId = NumberUtils.toLong(id.asString(), 0);
        if (userId == 0) {
            return null;
        }
        User user = SpringContext.getBean(UserCacheManager.class).getUser(userId);
        boolean isSameToken = user != null && Objects.equals(user.getToken(), token);
        if (isSameToken) {
            return user;
        }
        return null;
    }

}
