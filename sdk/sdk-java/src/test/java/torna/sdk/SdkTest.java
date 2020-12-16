package torna.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import torna.sdk.client.OpenClient;
import torna.sdk.common.Booleans;
import torna.sdk.param.DocItem;
import torna.sdk.param.DocParamCode;
import torna.sdk.param.DocParamHeader;
import torna.sdk.param.DocParamReq;
import torna.sdk.param.DocParamResp;
import torna.sdk.param.EnumInfoParam;
import torna.sdk.param.EnumItemParam;
import torna.sdk.request.DocCategoryCreateRequest;
import torna.sdk.request.DocCategoryListRequest;
import torna.sdk.request.DocCategoryNameUpdateRequest;
import torna.sdk.request.DocGetRequest;
import torna.sdk.request.DocListRequest;
import torna.sdk.request.DocPushRequest;
import torna.sdk.request.EnumPushRequest;
import torna.sdk.response.BaseResponse;
import torna.sdk.response.DocCategoryCreateResponse;
import torna.sdk.response.DocCategoryListResponse;
import torna.sdk.response.DocCategoryNameUpdateResponse;
import torna.sdk.response.DocGetResponse;
import torna.sdk.response.DocListResponse;
import torna.sdk.response.DocPushResponse;
import torna.sdk.response.EnumPushResponse;
import torna.sdk.result.DocDetailResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SdkTest extends BaseTest {

    static String url = "http://tornaserver.opensphere.cn/api";
    static String appKey = "20201216788730160688922624";
    static String secret = "KQW2Y?ETgKfjlmt1#.Jkw.tdD@1HSTbd";
    static String token = "c16931fa6590483fb7a4e85340fcbfef";


    static OpenClient client = new OpenClient(url, appKey, secret);

    /**
     * 获取文档列表
     */
    public void testDocList() {
        DocListRequest request = new DocListRequest(token);

        DocListResponse response = client.execute(request);
        this.printResponse(response);
    }

    /**
     * 获取文档信息
     */
    public void testDocGetRequest() {
        // 创建请求对象
        DocGetRequest request = new DocGetRequest(token);
        // 设置请求参数
        request.setId("eW2onQzD");

        // 发送请求
        DocGetResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            DocDetailResult data = response.getData();
            System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
        this.printResponse(response);
    }

    /**
     * 创建分类
     */
    public void testDocCategoryCreateRequest() {
        DocCategoryCreateRequest request = new DocCategoryCreateRequest(token);
        request.setName("产品分类2");

        DocCategoryCreateResponse response = client.execute(request);
        this.printResponse(response);
    }

    /**
     * 获取分类
     */
    public void testDocCategoryListRequest() {
        DocCategoryListRequest request = new DocCategoryListRequest(token);

        DocCategoryListResponse response = client.execute(request);
        this.printResponse(response);
    }

    /**
     * 修改分类名称
     */
    public void testDocCategoryNameUpdateRequest() {
        DocCategoryNameUpdateRequest request = new DocCategoryNameUpdateRequest(token);
        request.setId("jP81Owzq");
        request.setName("产品分类3");

        DocCategoryNameUpdateResponse response = client.execute(request);
        this.printResponse(response);
    }

    /**
     * 推送文档
     */
    public void testDocPushRequest() {
        long time = System.currentTimeMillis();
        DocPushRequest request = new DocPushRequest(token);
        // 分类
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        folder.setName("推送目录" + time);

        List<DocItem> items = new ArrayList<>(8);
        // 分类下面有文档
        folder.setItems(items);

        // 创建三个文档
        for (int i = 0; i < 3; i++) {
            DocItem docItem = buildDocItem(time, i);
            items.add(docItem);
        }
        // 再创建一个没有分类的文档
        DocItem docItem = buildDocItem(time, items.size() + 1);

        request.setBaseUrl("http://localhost:8090");
        request.setApis(Arrays.asList(folder, docItem));

        DocPushResponse response = client.execute(request);
        this.printResponse(response);
    }

    private static DocItem buildDocItem(long time,int i) {
        String suffix = "_" + time + "_" + i;
        DocItem item = new DocItem();
        item.setName("推送文档名称" + suffix);
        item.setDescription("推送文档描述" + suffix);
        item.setUrl("/get" + suffix);
        item.setHttpMethod("GET");
        item.setContentType("application/json");
        item.setParentId("");
        item.setIsShow(Booleans.TRUE);
        // 设置header
        DocParamHeader header = new DocParamHeader();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("token" + suffix);
        header.setExample("112233" + suffix);
        item.setHeaderParams(Arrays.asList(header));

        // 设置请求参数
        DocParamReq paramCreateParamReq = new DocParamReq();
        paramCreateParamReq.setName("goodsName" + suffix);
        paramCreateParamReq.setType("string");
        paramCreateParamReq.setDescription("商品名称" + suffix);
        paramCreateParamReq.setExample("iphone12" + suffix);
        paramCreateParamReq.setMaxLength("64");
        paramCreateParamReq.setRequired(Booleans.TRUE);
        paramCreateParamReq.setParentId("");
        item.setRequestParams(Arrays.asList(paramCreateParamReq));

        // 设置返回参数
        DocParamResp paramCreateParamResp = new DocParamResp();
        paramCreateParamResp.setName("id");
        paramCreateParamResp.setType("int");
        paramCreateParamResp.setDescription("商品id" + suffix);
        paramCreateParamResp.setExample("22");
        paramCreateParamResp.setParentId("");

        DocParamResp paramCreateParamResp2 = new DocParamResp();
        paramCreateParamResp2.setName("gender");
        paramCreateParamResp2.setType("enum");
        paramCreateParamResp2.setDescription("性别枚举" + suffix);
        paramCreateParamResp2.setExample("1");
        paramCreateParamResp2.setParentId("");
        // 枚举信息
        paramCreateParamResp2.setEnumInfo(getEnumInfoParam());
        item.setResponseParams(Arrays.asList(paramCreateParamResp, paramCreateParamResp2));

        // 设置错误码
        DocParamCode code = new DocParamCode();
        code.setCode("10001");
        code.setMsg("token错误");
        code.setSolution("请传token" + suffix);
        item.setErrorCodeParams(Arrays.asList(code));

        return item;
    }

    private static EnumInfoParam getEnumInfoParam() {
        EnumInfoParam enumInfoParam = new EnumInfoParam();
        enumInfoParam.setName("性别");
        EnumItemParam man = new EnumItemParam();
        man.setName("1");
        man.setType("byte");
        man.setValue("1");
        man.setDescription("男");

        EnumItemParam woman = new EnumItemParam();
        woman.setName("0");
        woman.setType("byte");
        woman.setValue("0");
        woman.setDescription("女");

        enumInfoParam.setItems(Arrays.asList(man, woman));
        return enumInfoParam;
    }

    /**
     * 推送字典
     */
    public void testEnumPushRequest() {
        EnumPushRequest request = new EnumPushRequest(token);
        request.setName("字典推送");
        List<EnumItemParam> items = new ArrayList<>(8);
        for (int i = 0; i < 3; i++) {
            EnumItemParam item = new EnumItemParam();
            item.setName("name2" + i);
            item.setType("string");
            item.setValue("v" + i);
            item.setDescription("描述2" + i);
            items.add(item);
        }
        request.setItems(items);
        request.setDescription("描述");
        EnumPushResponse response = client.execute(request);
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
