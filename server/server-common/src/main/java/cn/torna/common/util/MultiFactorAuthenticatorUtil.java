package cn.torna.common.util;

import cn.torna.common.bean.EnvironmentKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 谷歌身份验证器工具类
 *
 * @author qiuyu
 */
@Slf4j
public final class MultiFactorAuthenticatorUtil {
    private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final int SECRET_SIZE = 10;
    private static final int WINDOW_SIZE = 1;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;

    private static final Base32 BASE_32 = new Base32();

    private MultiFactorAuthenticatorUtil() {
    }

    /**
     * 生成令牌秘钥
     *
     * @return 令牌秘钥，如果生成失败则返回null
     */
    public static String generateSecretKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(EnvironmentKeys.TORNA_MFA_SEED.getValue()));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            return BASE_32.encodeToString(buffer);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate secret key", e);
            return null;
        }
    }

    /**
     * 生成Base64格式的二维码图片
     *
     * @param user   用户名，不能为空
     * @param secret 令牌秘钥，不能为空
     * @return Base64图片字符串
     * @throws IllegalArgumentException 如果参数为空
     */
    public static String getQRBarcode(String user, String secret) {
        if (user == null || user.trim().isEmpty() || secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("User and secret must not be empty");
        }

        String format = "otpauth://totp/%s?secret=%s&issuer=%s";
        String imageContent = String.format(format, user, secret, EnvironmentKeys.TORNA_MFA_ISSUER.getValue());
        log.debug("Generating QR code for: {}", imageContent);
        return QRCodeUtil.getBase64QRCode(imageContent);
    }

    /**
     * 验证令牌码
     *
     * @param secret 令牌秘钥，不能为空
     * @param code   令牌码
     * @param time   当前时间(毫秒)
     * @return 验证成功返回true，否则返回false
     * @throws IllegalArgumentException 如果secret为空
     * @throws RuntimeException         如果发生加密相关错误
     */
    public static boolean checkCode(String secret, long code, long time) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret must not be empty");
        }

        byte[] decodedKey = BASE_32.decode(secret);
        long timeWindow = (time / 1000L) / TIME_STEP_SECONDS;

        // 检查窗口范围内的代码
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
            try {
                if (verifyCode(decodedKey, timeWindow + i) == code) {
                    return true;
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                log.error("Failed to verify code", e);
                throw new RuntimeException("Authentication error", e);
            }
        }

        return false;
    }

    /**
     * 验证代码
     *
     * @param key 解码后的密钥
     * @param t   时间窗口
     * @return 生成的验证码
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static long verifyCode(byte[] key, long t)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        for (int i = 8; i-- > 0; t >>>= 8) {
            data[i] = (byte) t;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        return truncatedHash % (long) Math.pow(10, CODE_DIGITS);
    }
}