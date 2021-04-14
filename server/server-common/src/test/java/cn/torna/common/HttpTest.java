package cn.torna.common;

import cn.torna.common.bean.HttpHelper;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author tanghc
 */
public class HttpTest {


    /**
     * 普通get请求
     * @throws IOException
     */
    @Test
    public void get() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute()
                .asString();
        System.out.println(s);
    }

    /**
     * post提交表单
     * @throws IOException
     */
    @Test
    public void postForm() throws IOException {
        Map<String, Object> form = new HashMap<>();
        form.put("id", 1);
        String s = HttpHelper.postForm("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", form)
                .execute()
                .asString();
        System.out.println(s);
    }

    /**
     * post提交表单，并上传文件。multipart
     * @throws IOException
     */
    @Test
    public void postFormFile() throws IOException {
        Map<String, Object> form = new HashMap<>();
        HttpHelper.UploadFile file = new HttpHelper.UploadFile("name", "a.txt", "hello world".getBytes());
        form.put("id", 1);
        String s = HttpHelper.postForm("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", form, file)
                .execute()
                .asString();
        System.out.println(s);
    }

    /**
     * post提交json
     * @throws IOException
     */
    @Test
    public void postJson() throws IOException {
        Map<String, Object> form = new HashMap<>();
        String json = JSON.toJSONString(form);
        String s = HttpHelper.postJson("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", json)
                .execute()
                .asString();
        System.out.println(s);
    }

    /**
     * 设置header
     * @throws IOException
     */
    @Test
    public void setHeader() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                // 设置单个header
                .header("token", "xxx")
                .execute()
                .asString();
        System.out.println(s);

        Map<String, String> headers = new HashMap<>();
        headers.put("token", "xxx");
        String s2 = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                // 设置全部header
                .headers(headers)
                .execute()
                .asString();
        System.out.println(s2);
    }

    /**
     * 返回流
     * @throws IOException
     */
    @Test
    public void getStream() throws IOException {
        InputStream inputStream = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute()
                .asStream();
        System.out.println(inputStream);
    }

    /**
     * 获取http状态
     * @throws IOException
     */
    @Test
    public void getStatus() throws IOException {
        HttpHelper.ResponseResult responseResult = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute();
        int status = responseResult.getStatus();
        if (status == HttpStatus.SC_OK) {
            System.out.println(responseResult.asString());
        }
    }

    /**
     * 获取响应头
     * @throws IOException
     */
    @Test
    public void getHeaders() throws IOException {
        HttpHelper.ResponseResult responseResult = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute();
        Map<String, String> headers = responseResult.getHeaders();
        System.out.println(headers);
        System.out.println(responseResult.asString());
    }

    /**
     * 获取HttpResponse对象
     * @throws IOException
     */
    @Test
    public void getResponse() throws IOException {
        HttpHelper.ResponseResult responseResult = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute();
        HttpResponse response = responseResult.getResponse();
        Locale locale = response.getLocale();
        System.out.println(locale);
    }

    /**
     * 自定义
     * @throws IOException
     */
    @Test
    public void custom() throws IOException {
        String s = HttpHelper.create()
                .url("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .method("get")
                .header("token", "xx")
                .execute()
                .asString();
        System.out.println(s);

        String json = "{ \"name\": \"Jim\" }";
        String s2 = HttpHelper.create()
                .url("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .method("POST")
                .header("token", "xx")
                .entity(new StringEntity(json, ContentType.APPLICATION_JSON))
                .execute()
                .asString();
        System.out.println(s2);
    }

}
