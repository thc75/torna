package torna.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import torna.common.exception.JwtCreateException;
import torna.common.exception.JwtErrorException;
import torna.common.exception.JwtExpiredException;
import torna.common.exception.LoginFailureException;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static Map<String, Claim> verifyJwt(String token, String secret) {
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
        }
        /*
         * @throws AlgorithmMismatchException if the algorithm stated in the
         * token's header it's not equal to the one defined in the {@link
         * JWTVerifier}.
         *
         * @throws SignatureVerificationException if the signature is invalid.
         *
         * @throws TokenExpiredException if the token has expired.
         *
         * @throws InvalidClaimException if a claim contained a different value
         * than the expected one.
         */
        catch (TokenExpiredException e) {
            throw new JwtExpiredException();
        }
        catch (Exception e) {
            log.error("验证jwt失败", e);
            throw new LoginFailureException();
        }

        return jwt.getClaims();
    }
}
