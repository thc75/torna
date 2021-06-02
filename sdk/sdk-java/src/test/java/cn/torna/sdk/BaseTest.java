package cn.torna.sdk;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.response.BaseResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;

public class BaseTest extends TestCase {

    static String url = "http://localhost:7700/api";
    static String appKey = "20210527847416013153632256";
    static String secret = "7zMFQr-oJ7NxOrHSyWlmNKrwy35b>xwZ";
    static String token = "9eb04190b4e1463d80f93c5c4bb8ffa9";


    static OpenClient client = new OpenClient(url, appKey, secret);

    protected void printResponse(BaseResponse<?> response) {
        if (response.isSuccess()) {
            // 返回结果
            Object data = response.getData();
            System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }
}
