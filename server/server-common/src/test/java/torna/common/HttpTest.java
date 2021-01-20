package torna.common;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import torna.common.bean.HttpHelper;
import torna.common.bean.HttpTool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class HttpTest {

    HttpTool httpTool = new HttpTool();

    @Test
    public void testDo() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "blog.csdn.net");
        String s = httpTool.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186", headers);
        System.out.println(s);
    }

    @Test
    public void testGet() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186");
        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String s = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
                System.out.println(s);
            }
        }
    }

    @Test
    public void testGet2() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute()
                .asString();
        System.out.println(s);
    }

}
