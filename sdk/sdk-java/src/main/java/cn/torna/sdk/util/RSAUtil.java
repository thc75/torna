package cn.torna.sdk.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具<br>
 */
public class RSAUtil {
    public static String RSA_ALGORITHM = "RSA";
    public static String UTF8 = "UTF-8";


    /**
     * 获取公钥对象
     *
     * @param pubKeyData
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(byte[] pubKeyData) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取公钥对象
     *
     * @param pubKey 公钥
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String pubKey) throws Exception {
        return getPublicKey(Base64Util.decodeBase64(pubKey));

    }

    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        return encryptByPublicKey(data, getPublicKey(publicKey));
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(data.getBytes(UTF8));
        return Base64Util.encodeBase64String(bytes);
    }

    public static String decryptByPublicKey(String data, String rsaPublicKey) throws Exception {
        return decryptByPublicKey(data, getPublicKey(rsaPublicKey));
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param rsaPublicKey
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String data, RSAPublicKey rsaPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        byte[] inputData = Base64Util.decodeBase64(data);
        byte[] bytes = cipher.doFinal(inputData);
        return new String(bytes, UTF8);
    }

}