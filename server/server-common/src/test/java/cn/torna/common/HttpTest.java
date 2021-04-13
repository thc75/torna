package cn.torna.common;

import cn.torna.common.bean.HttpHelper;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class HttpTest {


    @Test
    public void get() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute()
                .asString();
        System.out.println(s);
    }

    @Test
    public void postForm() throws IOException {
        Map<String, Object> form = new HashMap<>();
        form.put("id", 1);
        String s = HttpHelper.postForm("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", form)
                .execute()
                .asString();
        System.out.println(s);
    }

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

    @Test
    public void postJson() throws IOException {
        Map<String, Object> form = new HashMap<>();
        String json = JSON.toJSONString(form);
        String s = HttpHelper.postJson("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", json)
                .execute()
                .asString();
        System.out.println(s);
    }

    @Test
    public void setHeader() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .setHeader("token", "xxx")
                .execute()
                .asString();
        System.out.println(s);

        Map<String, String> headers = new HashMap<>();
        String s2 = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .headers(headers)
                .execute()
                .asString();
        System.out.println(s2);
    }

}
