package torna.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import torna.sdk.client.OpenClient;
import torna.sdk.common.Booleans;
import torna.sdk.param.CodeParamCreateParam;
import torna.sdk.param.CodeParamUpdateParam;
import torna.sdk.param.DocParamCreateParam;
import torna.sdk.param.DocParamUpdateParam;
import torna.sdk.param.HeaderParamCreateParam;
import torna.sdk.param.HeaderParamUpdateParam;
import torna.sdk.request.DocCategoryCreateRequest;
import torna.sdk.request.DocCategoryListRequest;
import torna.sdk.request.DocCategoryNameUpdateRequest;
import torna.sdk.request.DocCreateRequest;
import torna.sdk.request.DocGetRequest;
import torna.sdk.request.DocListRequest;
import torna.sdk.request.DocUpdateRequest;
import torna.sdk.response.BaseResponse;
import torna.sdk.response.DocCategoryCreateResponse;
import torna.sdk.response.DocCategoryListResponse;
import torna.sdk.response.DocCategoryNameUpdateResponse;
import torna.sdk.response.DocCreateResponse;
import torna.sdk.response.DocGetResponse;
import torna.sdk.response.DocListResponse;
import torna.sdk.response.DocUpdateResponse;
import torna.sdk.result.DocDetailResult;

import java.util.Arrays;

public class SdkTest extends BaseTest {

    static String url = "http://localhost:7700/api";
    static String appKey = "20201127781912960996999168";
    static String secret = "ltatugCHeRJzCvjVxF39A%6.F$eV#~~L";
    static String token = "e807db2eb8564c4b89caf5a2f2d15b77";


    static OpenClient client = new OpenClient(url, appKey, secret);

    // 获取文档列表
    public void testDocList() {
        DocListRequest request = new DocListRequest(token);

        DocListResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 获取文档信息
    public void testDocGetRequest() {
        // 创建请求对象
        DocGetRequest request = new DocGetRequest(token);
        // 设置请求参数
        request.setId("je24ozLJ");

        // 发送请求
        DocGetResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 创建分类
    public void testDocCategoryCreateRequest() {
        DocCategoryCreateRequest request = new DocCategoryCreateRequest(token);
        request.setName("产品分类2");

        DocCategoryCreateResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 获取分类
    public void testDocCategoryListRequest() {
        DocCategoryListRequest request = new DocCategoryListRequest(token);

        DocCategoryListResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 修改分类名称
    public void testDocCategoryNameUpdateRequest() {
        DocCategoryNameUpdateRequest request = new DocCategoryNameUpdateRequest(token);
        request.setId("je24ozLJ");
        request.setName("新name");

        DocCategoryNameUpdateResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 创建文档
    public void testDocCreateRequest() {
        DocCreateRequest request = new DocCreateRequest(token);
        request.setName("文档a");
        request.setDescription("描述a");
        request.setUrl("/get");
        request.setHttpMethod("GET");
        request.setContentType("application/json");
        request.setParentId("");
        request.setIsShow(Booleans.TRUE);
        // 设置header
        HeaderParamCreateParam header = new HeaderParamCreateParam();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("token");
        header.setExample("aasdf");
        request.setHeaderParams(Arrays.asList(header));

        // 设置请求参数
        DocParamCreateParam paramCreateParamReq = new DocParamCreateParam();
        paramCreateParamReq.setName("goodsName");
        paramCreateParamReq.setType("string");
        paramCreateParamReq.setDescription("商品名称");
        paramCreateParamReq.setExample("iphone12");
        paramCreateParamReq.setMaxLength("64");
        paramCreateParamReq.setRequired(Booleans.TRUE);
        paramCreateParamReq.setParentId("");
        request.setRequestParams(Arrays.asList(paramCreateParamReq));

        // 设置返回参数
        DocParamCreateParam paramCreateParamResp = new DocParamCreateParam();
        paramCreateParamResp.setName("id");
        paramCreateParamResp.setType("int");
        paramCreateParamResp.setDescription("商品id");
        paramCreateParamResp.setExample("22");
        paramCreateParamResp.setParentId("");
        request.setResponseParams(Arrays.asList(paramCreateParamResp));

        CodeParamCreateParam code = new CodeParamCreateParam();
        code.setCode("10001");
        code.setMsg("token错误");
        code.setSolution("请传token");
        request.setErrorCodeParams(Arrays.asList(code));

        DocCreateResponse response = client.execute(request);
        this.printResponse(response);
    }

    // 修改文档
    public void testDocUpdateRequest() {
        DocUpdateRequest request = new DocUpdateRequest(token);
        request.setId("awXPMqzn");
        request.setName("文档b");
        request.setDescription("描述b");
        request.setUrl("/get/b");
        request.setHttpMethod("POST");
        request.setContentType("application/json");
        request.setParentId("");
        request.setIsShow(Booleans.TRUE);
        // 设置header
        HeaderParamUpdateParam header = new HeaderParamUpdateParam();
        header.setId("jP81wdzq");
        header.setName("tokenb");
        header.setRequired(Booleans.TRUE);
        header.setDescription("tokenb");
        header.setExample("aasdfb");
        request.setHeaderParams(Arrays.asList(header));

        // 设置请求参数
        DocParamUpdateParam paramCreateParamReq = new DocParamUpdateParam();
        paramCreateParamReq.setId("L42Ggy2W");
        paramCreateParamReq.setName("goodsNameb");
        paramCreateParamReq.setType("string");
        paramCreateParamReq.setDescription("商品名称b");
        paramCreateParamReq.setExample("iphone12b");
        paramCreateParamReq.setMaxLength("60");
        paramCreateParamReq.setRequired(Booleans.TRUE);
        paramCreateParamReq.setParentId("");
        request.setRequestParams(Arrays.asList(paramCreateParamReq));

        // 设置返回参数
        DocParamUpdateParam paramCreateParamResp = new DocParamUpdateParam();
        paramCreateParamResp.setId("9m8wapzk");
        paramCreateParamResp.setName("idb");
        paramCreateParamResp.setType("int");
        paramCreateParamResp.setDescription("商品idv");
        paramCreateParamResp.setExample("22v");
        paramCreateParamResp.setParentId("");
        request.setResponseParams(Arrays.asList(paramCreateParamResp));

        CodeParamUpdateParam code = new CodeParamUpdateParam();
        code.setId("3E8reL20");
        code.setCode("10001b");
        code.setMsg("token错误b");
        code.setSolution("请传tokenb");
        request.setErrorCodeParams(Arrays.asList(code));

        DocUpdateResponse response = client.execute(request);
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
