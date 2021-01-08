package torna.web.controller.doc;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import torna.common.bean.HttpTool;
import torna.common.util.UploadUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc")
@Slf4j
public class DebugController {

    private static final String HEADER_TARGET_URL = "target-url";
    private static final HttpTool HTTP_TOOL = new HttpTool();

    /**
     * 代理转发，前端调试请求转发到具体的服务器
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping("/debug")
    public void proxy(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String url = httpServletRequest.getHeader(HEADER_TARGET_URL);
        String method = httpServletRequest.getMethod();
        String contentType = httpServletRequest.getContentType();
        Map<String, String> headers = this.getHeaders(httpServletRequest);
        String queryString = httpServletRequest.getQueryString();
        if (StringUtils.hasLength(queryString)) {
            url = url + "?" + queryString;
        }

        RequestBody requestBody = null;
        if (contentType != null) {
            String contentTypeLower = contentType.toLowerCase();
            // 如果是文件上传
            if (contentTypeLower.contains("multipart")) {
                requestBody = getMultipartBody(httpServletRequest);
            } else if (contentTypeLower.contains("x-www-form-urlencoded")) {
                requestBody = getFormBody(httpServletRequest);
            } else {
                requestBody = getApplicationBody(httpServletRequest);
            }
        }

        Request.Builder requestBuilder = new Request.Builder().url(url);
        // 添加header
        headers.remove("host");
        headers.remove("accept-encoding");
        HttpTool.addHeader(requestBuilder, headers);

        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder = requestBuilder.get();
                break;
            case "POST":
                requestBuilder = requestBuilder.post(requestBody);
                break;
            case "PUT":
                requestBuilder = requestBuilder.put(requestBody);
                break;
            case "PATCH":
                requestBuilder = requestBuilder.patch(requestBody);
                break;
            case "DELETE":
                requestBuilder = requestBuilder.delete(requestBody);
                break;
            case "HEAD":
                requestBuilder = requestBuilder.head();
                break;
            default: {
                requestBuilder = requestBuilder.get();
            }
        }

        Request request = requestBuilder.build();
        try (Response response = HTTP_TOOL.getHttpClient().newCall(request).execute()) {
            Map<String, List<String>> headersMap = response.headers().toMultimap();
            Map<String, String> targetHeaders = new HashMap<>(headersMap.size() * 2);
            headersMap.forEach((key, value) -> {
                String headerValue = String.join(",", value);
                httpServletResponse.setHeader(key, headerValue);
                targetHeaders.put(key, headerValue);
            });
            httpServletResponse.addHeader("target-response-headers", JSON.toJSONString(targetHeaders));
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
            }
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            log.error("请求异常, url:{}",url, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private RequestBody getMultipartBody(HttpServletRequest request) {
        // 创建MultipartBody.Builder，用于添加请求的数据
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        Collection<MultipartFile> files = UploadUtil.getUploadFiles(request);
        for (MultipartFile uploadFile : files) {
            // 请求的名字
            try {
                bodyBuilder.addFormDataPart(
                        // 表单名
                        uploadFile.getName(),
                        // 文件名，服务器端用来解析的
                        uploadFile.getOriginalFilename(),
                        // 创建RequestBody，把上传的文件放入
                        RequestBody.create(null, uploadFile.getBytes())
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 设置文本参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue()[0]);
        }
        return bodyBuilder.build();
    }

    private RequestBody getFormBody(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        FormBody.Builder paramBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            paramBuilder.add(entry.getKey(), entry.getValue()[0]);
        }
        return paramBuilder.build();
    }

    private RequestBody getApplicationBody(HttpServletRequest request) {
        byte[] body = new byte[0];
        try {
            ServletInputStream inputStream = request.getInputStream();
            body = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RequestBody.create(MediaType.parse(request.getContentType()), body);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>(8);
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (!HEADER_TARGET_URL.equals(name)) {
                headers.put(name, request.getHeader(name));
            }
        }
        return headers;
    }

}
