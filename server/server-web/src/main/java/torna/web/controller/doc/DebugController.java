package torna.web.controller.doc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import torna.common.bean.HttpHelper;
import torna.common.util.UploadUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/debug")
@Slf4j
public class DebugController {

    private static final String HEADER_TARGET_URL = "target-url";
    private static final String HEADER_TARGET_HEADERS = "target-headers";

    /**
     * 代理转发，前端调试请求转发到具体的服务器
     */
    @RequestMapping("/v1")
    public void proxy(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getHeader(HEADER_TARGET_URL);
        String method = request.getMethod();
        Headers headers = this.getHeaders(request);
        String contentType = headers.getValue("Content-Type");
        String queryString = request.getQueryString();
        if (StringUtils.hasLength(queryString)) {
            url = url + "?" + queryString;
        }

        HttpHelper httpHelper;
        if (contentType != null) {
            String contentTypeLower = contentType.toLowerCase();
            // 如果是文件上传
            if (contentTypeLower.contains("multipart")) {
                Map<String, String> form = getForm(request);
                List<HttpHelper.UploadFile> multipartFiles = getMultipartFiles(request);
                httpHelper = HttpHelper.postForm(url, form, multipartFiles.toArray(new HttpHelper.UploadFile[0]));
            } else if (contentTypeLower.contains("x-www-form-urlencoded")) {
                Map<String, String> form = getForm(request);
                httpHelper = HttpHelper.postForm(url, form);
            } else if (contentTypeLower.contains("json")) {
                String text = getText(request);
                httpHelper = HttpHelper.postJson(url, text);
            } else {
                String text = getText(request);
                httpHelper = HttpHelper.postText(url, text, contentType);
            }
        } else {
            httpHelper = HttpHelper
                    .create()
                    .url(url)
                    .method(method);
        }
        httpHelper.headers(headers);

        HttpHelper.ResponseResult responseResult = null;
        try {
            responseResult = httpHelper.execute();
            Map<String, String> targetHeaders = responseResult.getHeaders();
            response.addHeader("target-response-headers", JSON.toJSONString(targetHeaders));
            InputStream inputStream = responseResult.asStream();
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("请求异常, url:{}", url, e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            httpHelper.closeResponse();
        }
    }

    private List<HttpHelper.UploadFile> getMultipartFiles(HttpServletRequest request) {
        Collection<MultipartFile> files = UploadUtil.getUploadFiles(request);
        return files.stream()
                .map(multipartFile -> {
                    try {
                        return new HttpHelper.UploadFile(multipartFile.getName(),
                                multipartFile.getOriginalFilename(),
                                multipartFile.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, String> getForm(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null) {
            return Collections.emptyMap();
        }
        Map<String, String> form = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            form.put(entry.getKey(), entry.getValue()[0]);
        }
        return form;
    }

    private String getText(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Headers getHeaders(HttpServletRequest request) {
        String header = request.getHeader(HEADER_TARGET_HEADERS);
        if (StringUtils.isEmpty(header)) {
            header = "{}";
        }
        JSONObject headersJson = JSON.parseObject(header);
        Headers headers = new Headers(headersJson);
        String contentType = request.getContentType();
        headers.put("Content-Type", contentType);
        return headers;
    }

    static class Headers extends HashMap<String, String> {

        public Headers() {
        }

        public Headers(Map<? extends String, ?> m) {
            m.forEach((key, value) -> {
                put(key, value.toString());
            });
        }

        public String getValue(String name) {
            for (Entry<String, String> entry : this.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

}
