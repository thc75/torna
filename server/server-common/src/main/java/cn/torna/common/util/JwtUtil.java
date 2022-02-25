package cn.torna.common.util;

import cn.torna.common.exception.JwtCreateException;
import cn.torna.common.exception.JwtErrorException;
import cn.torna.common.exception.JwtExpiredException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tanghc
 */
@Slf4j
public class JwtUtil {

    private static final Map<String, Object> headerClaims = new HashMap<>();

    /**
     * 登录成功后跳转页面
     */
    private static final String SUCCESS_HTML = String.join("\n", Arrays.asList(
            "<html><head><script>",
            "localStorage.setItem('torna.token', '%s');",
            "location.href = '%s';",
            "</script></head><body></body></html>"
    ));

    static {
        headerClaims.put("typ", "JWT");
        headerClaims.put("alg", "HS256");
    }

    public static String createJwt(Map<String, String> data, int timeoutDays, String secret) {
        JWTCreator.Builder builder = JWT.create().withHeader(headerClaims);
        Set<Map.Entry<String, String>> entrySet = data.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            builder.withClaim(entry.getKey(), entry.getValue());
        }
        LocalDateTime expiredDay = LocalDateTime.now().plusDays(timeoutDays);
        Date expiredDate = Date.from(expiredDay.atZone(ZoneId.systemDefault()).toInstant());
        try {
            return builder
                    // 过期时间
                    .withExpiresAt(expiredDate)
                    // 创建时间
                    .withIssuedAt(new Date())
                    // 签名
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtCreateException();
        }
    }

    public static Map<String, Claim> verifyJwt(String token, String secret) throws JwtExpiredException, JwtErrorException {
        JWTVerifier verifier = null;
        try {
            verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        } catch (Exception e) {
            log.error("验证jwt失败", e);
            throw new JwtErrorException();
        }

        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new JwtExpiredException();
        } catch (Exception e) {
            log.error("验证jwt失败", e);
            throw new JwtErrorException();
        }

        return jwt.getClaims();
    }

    public static String getJumpPageHtml(String token) {
        return getJumpPageHtml(token, "/");
    }

    public static String getJumpPageHtml(String token, String redirectUrl) {
        return String.format(SUCCESS_HTML, token, redirectUrl);
    }
}
