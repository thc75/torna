package torna.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import torna.sdk.client.OpenClient;
import torna.sdk.param.DocCategoryCreateParam;
import torna.sdk.param.DocGetParam;
import torna.sdk.request.DocCategoryCreateRequest;
import torna.sdk.request.DocCategoryListRequest;
import torna.sdk.request.DocGetRequest;
import torna.sdk.request.DocListRequest;
import torna.sdk.response.BaseResponse;
import torna.sdk.response.DocCategoryCreateResponse;
import torna.sdk.response.DocCategoryListResponse;
import torna.sdk.response.DocGetResponse;
import torna.sdk.response.DocListResponse;

public class SdkTest extends BaseTest {

    static String url = "http://localhost:7700/api";
    static String appKey = "20201127781912960996999168";
    static String secret = "ltatugCHeRJzCvjVxF39A%6.F$eV#~~L";
    static String token = "e807db2eb8564c4b89caf5a2f2d15b77";


    static OpenClient client = new OpenClient(url, appKey, secret);

    public void testDocList() {
        DocListRequest request = new DocListRequest();
        request.setToken(token);

        DocListResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 获取文档信息
    public void testDocGetRequest() {
        // 创建请求对象
        DocGetRequest request = new DocGetRequest();
        // 设置请求参数
        request.setId("je24ozLJ");
        // 设置token
        request.setToken(token);

        // 发送请求
        DocGetResponse response = client.execute(request);

        this.printResponse(response);
    }

    // 创建分类
    public void testDocCategoryCreateRequest() {
        DocCategoryCreateRequest request = new DocCategoryCreateRequest();
        request.setName("产品分类2");
        request.setToken(token);

        DocCategoryCreateResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 获取分类
    public void testDocCategoryListRequest() {
        DocCategoryListRequest request = new DocCategoryListRequest();
        request.setToken(token);

        DocCategoryListResponse response = client.execute(request);
        this.printResponse(response);
    }

    private void printResponse(BaseResponse<?> response) {
        if (response.isSuccess()) {
            // 返回结果
            Object data = response.getData();
            System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

}
