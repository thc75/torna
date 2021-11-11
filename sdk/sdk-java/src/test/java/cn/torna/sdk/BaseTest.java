package cn.torna.sdk;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.response.BaseResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;

public class BaseTest extends TestCase {

    static String url = "http://localhost:7700/api";
    static String token = "c16931fa6590483fb7a4e85340fcbfef";


    static OpenClient client = new OpenClient(url);

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
