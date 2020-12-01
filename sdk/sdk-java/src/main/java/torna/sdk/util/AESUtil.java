package torna.sdk.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-128 ECB加密.<br>
 * 
 * <pre>
 * 字符集:UTF-8
 * 算法模式:ECB
 * 数据块:128位
 * 补码方式:PKCS5Padding
 * 加密结果编码方式:Base64
 * </pre>
 * 
 * @author tanghc
 *
 */
public class AESUtil {

    private static final String UTF8 = "UTF-8";
    private static final String ALGORITHM = "AES"; 
    private static final String ALGORITHM_CIPHER = "AES/ECB/PKCS5Padding";// 默认的加密算法

    /**
     * @param password 长度必须小于等于16
     * @return
     */
    public static SecretKey getSecretKey(String password) {
        byte[] passwordData = password.getBytes();
        if(passwordData.length > 16) {
            throw new IllegalArgumentException("password 长度必须小于等于16");
        }
        
        byte[] keyData = new byte[16]; // 创建一个空的16位字节数组（默认值为0）,16byte（128bit）
        System.arraycopy(passwordData, 0, keyData, 0, passwordData.length);
        
        return new SecretKeySpec(keyData, ALGORITHM);
    }

    /**
     * 加密
     * @param data 待加密数据
     * @param password 密码
     * @return 返回加密成功后数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKey(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);// Ciphr完成加密或解密工作类
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 对Cipher初始化，解密模式
        return cipher.doFinal(data);// 加密data
    }
    
    /**
     * 解密
     * @param data 待解密数据
     * @param password 密码
     * @return 返回解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKey(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);//Cipher完成加密或解密工作类
        cipher.init(Cipher.DECRYPT_MODE, secretKey);//对Cipher初始化，解密模式
        return cipher.doFinal(data);//解密data
    }

    /**
     * 文本加密
     * @param content 明文
     * @param password 密码
     * @return 返回base64内容
     * @throws Exception
     */
    public static String encryptToBase64String(String content, String password) throws Exception {
        byte[] data = content.getBytes(UTF8);
        byte[] result = encrypt(data, password);
        return Base64Util.encodeBase64String(result);
    }

    /**
     * 文本解密
     * @param base64String 待解密文本
     * @param password 密码
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptFromBase64String(String base64String, String password) throws Exception {
        byte[] data = Base64Util.decodeBase64(base64String);
        byte[] contentData = decrypt(data, password);
        return new String(contentData, UTF8);
    }


    /*public static void main(String[] args) throws Exception {
        String content = "我爱你";
        String password = "1234567890123456";
        System.out.println("password:" + password);

        String ret2 = encryptToBase64String(content, password);
        System.out.println("密文：" + ret2);
        String content3 = decryptFromBase64String(ret2, password);
        System.out.println(content.equals(content3));
    }*/

}
