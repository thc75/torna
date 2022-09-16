package cn.torna.web.controller.doc;

import cn.torna.common.bean.HttpHelper;
import cn.torna.common.util.RequestUtil;
import cn.torna.common.util.UploadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
    public static final String TARGET_RESPONSE_HEADERS_NAME = "target-response-headers";

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
        if (StringUtils.hasText(queryString)) {
            url = url + "?" + queryString;
        }

        HttpHelper httpHelper;
        // 针对上传文件需要特殊处理
        if (StringUtils.hasText(contentType) && contentType.toLowerCase().contains("multipart")) {
                Map<String, String> form = RequestUtil.getMultipartFields(request);
                List<HttpHelper.UploadFile> multipartFiles = getMultipartFiles(request);
                httpHelper = HttpHelper.postForm(url, form, multipartFiles.toArray(new HttpHelper.UploadFile[0]));
        } else {
            httpHelper = HttpHelper.create()
                    .url(url)
                    .method(method);
            try {
                ServletInputStream inputStream = request.getInputStream();
                byte[] bytes = IOUtils.toByteArray(inputStream);
                httpHelper.entity(new ByteArrayEntity(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        httpHelper.headers(headers);

        HttpHelper.ResponseResult responseResult = null;
        try {
            // 转发请求
            responseResult = httpHelper.execute();
            Map<String, String> targetHeaders = responseResult.getHeaders();
            response.addHeader(TARGET_RESPONSE_HEADERS_NAME, JSON.toJSONString(targetHeaders));
            int status = responseResult.getStatus();
            response.setStatus(status);
            InputStream inputStream = responseResult.asStream();
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error("请求异常, url:{}", url, e);
            try {
                IOUtils.copy(IOUtils.toInputStream(e.getMessage(), StandardCharsets.UTF_8), response.getOutputStream());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                // ignore
            }
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
        return new Headers(headersJson);
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
