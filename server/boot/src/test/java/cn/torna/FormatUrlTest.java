package cn.torna;

import org.junit.jupiter.api.Assertions;
import org.springframework.util.StringUtils;

/**
 * @author tanghc
 */
public class FormatUrlTest {

    public void testA() {
        String[] arr = {
                "/App/list",
                "App/list",
                "http://aaa.com:8080/App/list",
                "http://aaa.com/App/list",
                "https://aaa.com:8080/App/list",
                "https://aaa.com/App/list",
                "http:/bbb:8080/App/list",
                "http:bbb.com/App/list",
        };
        String HTTP = "http:";
        String HTTPS = "https:";
        for (String url : arr) {
            char split = '/';
            String urlLowerCase = url.toLowerCase();
            if (urlLowerCase.startsWith(HTTP)) {
                url = url.substring(HTTP.length());
                url = StringUtils.trimLeadingCharacter(url, split);
            } else if (urlLowerCase.startsWith(HTTPS)) {
                url = url.substring(HTTPS.length());
                url = StringUtils.trimLeadingCharacter(url, split);
            } else if (url.charAt(0) != split) {
                url = split + url;
            }
            int index = url.indexOf(split);
            if (index > 0) {
                url = url.substring(index);
            }
            Assertions.assertEquals("/App/list", url);
        }
    }
}
