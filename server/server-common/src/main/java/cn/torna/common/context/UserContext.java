package cn.torna.common.context;

import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.exception.ErrorTokenException;
import cn.torna.common.exception.JwtErrorException;
import cn.torna.common.exception.JwtExpiredException;
import cn.torna.common.exception.LoginFailureException;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.JwtUtil;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author tanghc
 */
@Slf4j
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
        try {
            return getUser(token);
        } catch (ErrorTokenException e) {
            throw new LoginFailureException();
        }
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_TOKEN);
        if (StringUtils.hasText(token) && token.startsWith(JWT_PREFIX)) {
            return token.substring(JWT_PREFIX.length());
        }
        return token;
    }

    public static String getPrefixUserId(String token) {
        if (StringUtils.isEmpty(token) || !token.contains(":")) {
            return null;
        }
        String[] tokenArr = token.split(":");
        return tokenArr[0];
    }

    /**
     * 获取登录用户
     * @param token 格式：<userId>:<jwt>
     * @return 返回token对应的用户，没有返回null
     */
    private static User getUser(String token) throws ErrorTokenException {
        if (StringUtils.isEmpty(token) || !token.contains(":")) {
            return null;
        }
        String[] tokenArr = token.split(":");
        String userIdStr = tokenArr[0];
        String jwt = tokenArr[1];
        Environment environment = SpringContext.getBean(Environment.class);
        String secret = environment.getProperty(SECRET_KEY);
        Map<String, Claim> data = null;
        Long userIdDecoded = IdUtil.decode(userIdStr);
        // verify jwt
        try {
            data = JwtUtil.verifyJwt(jwt, secret);
        } catch (JwtExpiredException | JwtErrorException e) {
            log.error("jwt verify failed, userId:{}, token:{}, message:{}", userIdDecoded, token, e.getMessage());
            throw new ErrorTokenException();
        }
        Claim id = data.get("id");
        long userId = verifyUserId(id, userIdDecoded);
        return SpringContext.getBean(UserCacheManager.class).getUser(userId);
    }

    /**
     * verify userId in token
     * @param id userId in jwt
     * @param userIdDecoded userId in token
     * @return return the true userId
     * @throws ErrorTokenException
     */
    private static long verifyUserId(Claim id, Long userIdDecoded) throws ErrorTokenException {
        if (id == null) {
            throw new ErrorTokenException();
        }
        long userId = NumberUtils.toLong(id.asString(), 0);
        if (!Objects.equals(userIdDecoded, userId)) {
            throw new ErrorTokenException();
        }
        return userId;
    }


    public static Locale getLocale() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            return request.getLocale();
        } catch (Exception e) {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }
}
