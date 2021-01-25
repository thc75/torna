package cn.torna.sdk.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SignUtil {

    private static final String MD5 = "MD5";
    private static final String ZERO = "0";

    /**
     * 构建签名
     * 
     * @param paramsMap
     *            参数
     * @param secret
     *            密钥
     * @return
     * @throws IOException
     */
    public static String createSign(Map<String, ?> paramsMap, String secret) {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList<String>(keySet);

        Collections.sort(paramNames);

        StringBuilder paramNameValue = new StringBuilder();

        for (String paramName : paramNames) {
            Object value = paramsMap.get(paramName);
            if (value != null) {
                paramNameValue.append(paramName).append(value);
            }
        }

        String source = secret + paramNameValue.toString() + secret;

        return MD5Util.encryptUpper(source);
    }

}
