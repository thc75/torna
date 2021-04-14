package cn.torna.common.bean;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP请求辅助类
 * <pre>
 * HttpHelper.ResponseResult responseResult = HttpHelper
 *  .create()
 *  .basicAuth(importSwaggerDTO.getBasicAuthUsername(), importSwaggerDTO.getBasicAuthPassword())
 *  .url(url)
 *  .method("get")
 *  .execute();
 *  String body = responseResult.asString();
 *  int status = responseResult.getStatus();
 * </pre>
 *
 * @author tanghc
 */
public class HttpHelper {

    private static final CloseableHttpClient CLOSEABLE_HTTP_CLIENT = HttpClients.createDefault();

    private RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(600000)
            .setSocketTimeout(600000)
            .build();

    private final HttpClient client;

    private HttpContext context;

    private String url;

    private HTTPMethod method;

    private HttpEntity entity;

    private Map<String, String> headers;

    private ResponseResult responseResult;

    public HttpHelper() {
        this(CLOSEABLE_HTTP_CLIENT);
    }

    public HttpHelper(HttpClient client) {
        this.client = client;
    }


    /**
     * 创建一个HttpHelper实例
     *
     * @return 返回HttpHelper
     */
    public static HttpHelper create() {
        return new HttpHelper();
    }


    /**
     * 设置basic认证
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回HttpHelper
     */
    public HttpHelper basicAuth(String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        this.context(context);
        return this;
    }

    /**
     * 设置HttpContext
     *
     * @param context context
     * @return 返回HttpHelper
     */
    public HttpHelper context(HttpContext context) {
        this.context = context;
        return this;
    }

    /**
     * 设置URL
     *
     * @param url url
     * @return 返回HttpHelper
     */
    public HttpHelper url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置method
     *
     * @param method method，GET、POST
     * @return 返回HttpHelper
     */
    public HttpHelper method(String method) {
        this.method = HTTPMethod.of(method);
        return this;
    }

    /**
     * 设置总的header
     *
     * @param headers headers
     * @return 返回HttpHelper
     */
    public HttpHelper headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 设置一个header
     *
     * @param name  name
     * @param value value
     * @return 返回HttpHelper
     */
    public HttpHelper header(String name, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>(8);
        }
        this.headers.put(name, value);
        return this;
    }

    /**
     * 设置entity
     * @param entity entity
     * @return 返回HttpHelper
     */
    public HttpHelper entity(HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 发送请求
     *
     * @return 返回响应结果
     * @throws IOException
     */
    public ResponseResult execute() throws IOException {
        HttpUriRequest request = this.buildHttpRequest();
        HttpResponse response = this.client.execute(request, this.context);
        this.responseResult = new DefaultResponseResult(response);
        return responseResult;
    }

    /**
     * 关闭response
     */
    public void closeResponse() {
        responseResult.closeResponse();
    }


    /**
     * 创建get请求
     *
     * @param url url
     * @return 返回HttpHelper
     */
    public static HttpHelper get(String url) {
        return create()
                .url(url)
                .method(HTTPMethod.GET.value());
    }

    /**
     * post请求json
     *
     * @param url  url
     * @param json json
     * @return 返回HttpHelper
     */
    public static HttpHelper postJson(String url, String json) {
        return postText(url, json, ContentType.APPLICATION_JSON.getMimeType());
    }

    /**
     * post请求文本
     *
     * @param url         url
     * @param text        文本内容
     * @param contentType contentType
     * @return 返回HttpHelper
     */
    public static HttpHelper postText(String url, String text, String contentType) {
        HttpHelper httpHelper = create()
                .url(url)
                .method(HTTPMethod.POST.value());
        StringEntity entity = new StringEntity(text, ContentType.create(contentType, StandardCharsets.UTF_8));
        entity.setChunked(true);
        httpHelper.entity = entity;
        return httpHelper;
    }

    /**
     * post表单
     *
     * @param url   url
     * @param form  表单参数
     * @param files 上传文件
     * @return 返回HttpHelper
     */
    public static HttpHelper postForm(String url, Map<String, ?> form, UploadFile... files) {
        if (files != null && files.length > 0) {
            return buildMultipart(url, form, files);
        } else {
            return buildForm(url, form);
        }
    }

    private static HttpHelper buildForm(String url, Map<String, ?> form) {
        HttpHelper httpHelper = post(url);
        List<NameValuePair> formParams = new ArrayList<>();
        if (form != null) {
            form.forEach((key, value) -> {
                formParams.add(new BasicNameValuePair(key, String.valueOf(value)));
            });
        }
        httpHelper.entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        return httpHelper;
    }

    private static HttpHelper buildMultipart(String url, Map<String, ?> form, UploadFile... files) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        // 设置浏览器兼容模式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // 设置参数
        if (form != null) {
            //设置post参数
            for (Map.Entry<String, ?> entry : form.entrySet()) {
                // 指定编码，防止中文乱码问题。但是某些情况下会导致上传失败
                builder.addTextBody(entry.getKey(),
                        String.valueOf(entry.getValue()),
                        ContentType.create("text/plain", Consts.UTF_8));
            }
        }
        // 设置上传文件
        if (files != null) {
            for (UploadFile file : files) {
                builder.addBinaryBody(file.getName(), file.getFileData(), ContentType.DEFAULT_BINARY, file.getFileName());
            }
        }
        HttpHelper httpHelper = post(url);
        httpHelper.entity = builder.build();
        return httpHelper;
    }

    private static HttpHelper post(String url) {
        return create()
                .url(url)
                .method(HTTPMethod.POST.value());
    }

    private HttpUriRequest buildHttpRequest() {
        HttpRequestBase request;
        Objects.requireNonNull(url, "url can not null");
        Objects.requireNonNull(method, "method can not null");
        switch (this.method) {
            case GET:
                request = new HttpGet(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            case HEAD:
                request = new HttpHead(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            case OPTIONS:
                request = new HttpOptions(url);
                break;
            default:
                throw new IllegalArgumentException("method error: " + this.method);

        }
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
        if (request instanceof HttpEntityEnclosingRequest && entity != null) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest) request;
            httpEntityEnclosingRequest.setEntity(this.entity);
        }
        request.setConfig(requestConfig);
        // 重新设置下contentType，如果是上传文件，contentType会改变
        if (entity != null) {
            request.setHeader(entity.getContentType());
        }
        return request;
    }

    enum HTTPMethod {
        /**
         * http GET
         */
        GET,
        /**
         * http POST
         */
        POST,
        /**
         * http PUT
         */
        PUT,
        /**
         * http HEAD
         */
        HEAD,
        /**
         * http DELETE
         */
        DELETE,
        /**
         * http OPTIONS
         */
        OPTIONS,
        ;

        private HTTPMethod() {
        }

        public String value() {
            return this.name();
        }

        public static HTTPMethod of(String v) {
            return valueOf(v.toUpperCase());
        }
    }

    /**
     * 结果包装类
     */
    public interface ResponseResult {
        int getStatus();

        String asString() throws IOException;

        InputStream asStream() throws IOException;

        Map<String, String> getHeaders();

        void closeResponse();

        HttpResponse getResponse();
    }

    static class DefaultResponseResult implements ResponseResult {
        private final HttpResponse response;

        public DefaultResponseResult(HttpResponse response) {
            this.response = response;
        }

        @Override
        public int getStatus() {
            return response.getStatusLine().getStatusCode();
        }

        /**
         * 返回字符串结果，使用UTF_8编码
         *
         * @return 返回字符串结果
         * @throws IOException
         */
        @Override
        public String asString() throws IOException {
            HttpEntity entity = this.response.getEntity();
            try {
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } finally {
                closeResponse();
            }
        }

        /**
         * InputStream使用完毕后，需要手动调用close()
         *
         * @return 返回InputStream
         * @throws IOException
         * @see #closeResponse()
         */
        @Override
        public InputStream asStream() throws IOException {
            HttpEntity entity = this.response.getEntity();
            return entity.getContent();
        }

        public Map<String, String> getHeaders() {
            Header[] allHeaders = response.getAllHeaders();
            if (allHeaders == null || allHeaders.length == 0) {
                return Collections.emptyMap();
            }
            Map<String, String> headers = new HashMap<>(allHeaders.length * 2);
            for (Header header : allHeaders) {
                headers.put(header.getName(), header.getValue());
            }
            return headers;
        }

        /**
         * 关闭response
         */
        @Override
        public void closeResponse() {
            if (response instanceof Closeable) {
                try {
                    ((Closeable) response).close();
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        public HttpResponse getResponse() {
            return response;
        }
    }

    /**
     * 文件上传类
     *
     * @author tanghc
     */
    public static class UploadFile implements Serializable {
        private static final long serialVersionUID = -1100614660944996398L;

        /**
         * @param name 表单名称，不能重复
         * @param file 文件
         * @throws IOException
         */
        public UploadFile(String name, File file) throws IOException {
            this(name, file.getName(), FileUtils.readFileToByteArray(file));
        }

        /**
         * @param name     表单名称，不能重复
         * @param fileName 文件名
         * @param input    文件流
         * @throws IOException
         */
        public UploadFile(String name, String fileName, InputStream input) throws IOException {
            this(name, fileName, IOUtils.toByteArray(input));
        }

        /**
         * @param name     表单名称，不能重复
         * @param fileName 文件名
         * @param fileData 文件数据
         */
        public UploadFile(String name, String fileName, byte[] fileData) {
            super();
            this.name = name;
            this.fileName = fileName;
            this.fileData = fileData;
        }

        private String name;
        private String fileName;
        private byte[] fileData;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getFileData() {
            return fileData;
        }

        public void setFileData(byte[] fileData) {
            this.fileData = fileData;
        }
    }

}
