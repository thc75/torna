package cn.torna;

import cn.torna.common.bean.HttpHelper;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author 六如
 */
public class DocApiTest {

    @Test
    public void test() throws IOException {
        String body = HttpHelper.get("http://localhost:7700/api")
                .parameter("name", "doc.list")
                .parameter("access_token", "36f711e5df884417a073782b2088696c")
                .execute()
                .asString();
        System.out.println(body);
    }

    @Test
    public void test2() throws IOException {
        JSONObject data = new JSONObject();
        data.put("docId", 124);
        String body = HttpHelper.get("http://localhost:7700/api")
                .parameter("name", "doc.detail")
                .parameter("access_token", "36f711e5df884417a073782b2088696c")
                .parameter("data", URLEncoder.encode(data.toJSONString(), "UTF-8"))
                .execute()
                .asString();
        System.out.println(body);
    }

    @Test
    public void test3() throws IOException {
        String body = HttpHelper.get("http://localhost:7700/api")
                .parameter("name", "module.get")
                .parameter("access_token", "36f711e5df884417a073782b2088696c")
                .execute()
                .asString();
        System.out.println(body);
    }

}
