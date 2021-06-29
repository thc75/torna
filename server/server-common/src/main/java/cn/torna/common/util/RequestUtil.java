package cn.torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tanghc
 */
@Slf4j
public class RequestUtil {

    private static final String IP_UNKNOWN = "unknown";
    private static final String IP_LOCAL = "127.0.0.1";
    private static final int IP_LEN = 15;

    /**
     * 获取表单中的字段，请求类型是application/x-www-form-urlencoded
     *
     * @param request request
     * @return 返回字段内容
     */
    public static Map<String, String> getFormFields(HttpServletRequest request) {
        String query;
        try {
            ServletInputStream inputStream = request.getInputStream();
            query = IOUtils.toString(inputStream, request.getCharacterEncoding());
        } catch (IOException e) {
            log.error("获取form参数失败", e);
            throw new RuntimeException("请求失败");
        }
        return parseQueryString(query);
    }

    public static Map<String, String> parseQueryString(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }
        query = StringUtils.trimLeadingCharacter(query, '?');
        String[] pairs = query.split("&");
        Map<String, String> form = new HashMap<>(pairs.length * 2);
        for (String pair : pairs) {
            String[] param = pair.split("=");
            String key = param[0];
            String value = UriUtils.decode(param[1], StandardCharsets.UTF_8);
            form.put(key, value);
        }
        return form;
    }

    /**
     * 获取上传文件表单中的字段，排除query参数
     *
     * @param request request
     * @return 返回字段内容
     */
    public static Map<String, String> getMultipartFields(HttpServletRequest request) {
        Map<String, String> queryParams = parseQueryString(request.getQueryString());
        Map<String, String> uploadParams = new HashMap<>(16);
        Set<String> queryKeys = queryParams.keySet();
        request.getParameterMap().forEach((key, value) -> {
            // 排除query param
            if (!queryKeys.contains(key)) {
                uploadParams.put(key, value[0]);
            }
        });
        return uploadParams;
    }

    /**
     * 获取客户端IP
     *
     * @param request request
     * @return 返回ip
     */
    public static String getIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (IP_LOCAL.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // ignore
                }
            }

        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > IP_LEN) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
