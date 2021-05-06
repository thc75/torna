package cn.torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
}
