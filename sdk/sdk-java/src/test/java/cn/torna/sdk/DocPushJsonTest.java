package cn.torna.sdk;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.request.DocPushDataRequest;
import cn.torna.sdk.response.DocPushResponse;
import cn.torna.sdk.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 用来演示推送纯json内容，可用于调试
 * @author tanghc
 */
public class DocPushJsonTest extends BaseTest {

    static String url = "http://localhost:7700/api";
    static String appKey = "20201216788835306945118208";
    static String secret = "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR";
    static String token = "3058bfa81fab4272bd32417141061df2";


    static OpenClient client = new OpenClient(url, appKey, secret);


    public void testDocPush() throws IOException {
        // 可将smart-doc日志中的data部分放到文件中，然后推送
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\Downloads\\data2.json"));
        byte[] bytes = FileUtil.toBytes(fileInputStream);
        String data = new String(bytes);
        System.out.println(data);
        DocPushDataRequest jsonRequest = new DocPushDataRequest(token, data);
        // 发送请求
        DocPushResponse response = client.execute(jsonRequest);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("请求成功");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }


}
