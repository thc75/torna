package torna.sdk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringUtil {
    public final static String UTF8 = "UTF-8";

    public static String encodeUrl(String input) {
        try {
            return URLEncoder.encode(input, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEmpty(String input) {
        if (input == null) {
            return true;
        }
        return "".equals(input.trim());
    }

    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }
}
