package cn.torna.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author tanghc
 */
public class DingTalkSignUtil {

    public static final String HMAC_SHA_256 = "HmacSHA256";

    /**
     * 个人免登场景的签名计算方法
     * @param appSecret appSecret
     * @param timestamp timestamp
     * @return 通过appSecret计算出来的签名值
     * @throws Exception
     */
    public static String getSignature(String appSecret, String timestamp) throws Exception {
        // 根据timestamp, appSecret计算签名值
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        byte[] signatureBytes = mac.doFinal(timestamp.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(signatureBytes));
    }

}
