package cn.torna.sdk;

import cn.torna.sdk.common.Booleans;
import cn.torna.sdk.param.DebugEnv;
import cn.torna.sdk.param.DocItem;
import cn.torna.sdk.param.DocParamCode;
import cn.torna.sdk.param.DocParamHeader;
import cn.torna.sdk.param.DocParamPath;
import cn.torna.sdk.param.DocParamReq;
import cn.torna.sdk.param.DocParamResp;
import cn.torna.sdk.param.EnumInfoParam;
import cn.torna.sdk.param.EnumItemParam;
import cn.torna.sdk.request.DocPushRequest;
import cn.torna.sdk.response.DocPushResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 模拟多线程推送
 * @author tanghc
 */
public class ConcurrentDocPushTest extends BaseTest {

    public void testDocPush() throws InterruptedException {
        final CountDownLatch count = new CountDownLatch(1);
        Map<String, String> map = new HashMap<>();
        map.put("A", "41aed17e31c44bff941cd9d047b532db");
        map.put("B", "ee8a128302124a1986ec778a2e6c1700");
        map.put("C", "b30174a555b745969b52aa06992efaeb");
        map.put("D", "b54d6d8d1d974791918b29d740865d9e");
        map.put("E", "2d3e79d55b1849afbb858adb800be54b");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            getThread(entry.getKey(), entry.getValue(), count).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count.countDown();
        Thread.sleep(3000);
    }

    private Thread getThread(final String name, final String token, final CountDownLatch count) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                DocPushRequest request = getRequest(name, token);
                try {
                    count.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                push(request);
            }
        });
    }

    private DocPushRequest getRequest(String name, String token) {
        DocPushRequest request = new DocPushRequest(token);
        // 创建分类
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        folder.setName("手机分类_" + name);

        List<DocItem> items = new ArrayList<>(8);
        // 分类下面有文档
        folder.setItems(items);

        // 创建三个文档
        for (int i = 0; i < 100; i++) {
            DocItem docItem = buildDocItem(i, name);
            items.add(docItem);
        }
        // 创建调试环境
        DebugEnv debugEnv = new DebugEnv("测试环境", "http://localhost:8090");

        // 设置请求参数
        request.setApis(Arrays.asList(folder));
        request.setDebugEnvs(Arrays.asList(debugEnv));
        return request;
    }

    private void push(DocPushRequest request) {
        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("请求成功");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    private static DocItem buildDocItem(int i, String name) {
        DocItem item = new DocItem();
        /* 设置基本信息 */
        item.setName("获取商品名称" + i + "_" + name);
        item.setDescription("这里是描述信息..." + i);
        item.setUrl("/goods/{id}/get" + i);
        item.setHttpMethod("GET");
        item.setContentType("application/json");
        item.setParentId("");
        item.setIsShow(Booleans.TRUE);

        /* 设置请求参数 */
        DocParamPath pathParam = new DocParamPath();
        pathParam.setName("id");
        pathParam.setType("int");
        pathParam.setDescription("id");
        pathParam.setExample("123");
        pathParam.setMaxLength("-");
        pathParam.setRequired(Booleans.TRUE);
        item.setPathParams(Arrays.asList(pathParam));

        /* 设置header */
        DocParamHeader header = new DocParamHeader();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("请求token");
        header.setExample("xxxx");
        item.setHeaderParams(Arrays.asList(header));

        /* 设置请求参数 */
        DocParamReq paramCreateParamReq = new DocParamReq();
        paramCreateParamReq.setName("goodsName");
        paramCreateParamReq.setType("string");
        paramCreateParamReq.setDescription("商品名称");
        paramCreateParamReq.setExample("iphone12");
        paramCreateParamReq.setMaxLength("64");
        paramCreateParamReq.setRequired(Booleans.TRUE);
        paramCreateParamReq.setParentId("");
        // 数组参数
        DocParamReq paramCreateParamReq2 = new DocParamReq();
        paramCreateParamReq2.setName("priceList");
        paramCreateParamReq2.setType("array"); // 注意这里是array
        paramCreateParamReq2.setDescription("价格");
        paramCreateParamReq2.setRequired(Booleans.TRUE);
        List<DocParamReq> children = buildChildren();
        paramCreateParamReq2.setChildren(children); // 设置子参数
        item.setRequestParams(Arrays.asList(paramCreateParamReq, paramCreateParamReq2));

        /* 设置返回参数 */
        DocParamResp paramCreateParamResp = new DocParamResp();
        // id参数
        paramCreateParamResp.setName("id");
        paramCreateParamResp.setType("int");
        paramCreateParamResp.setDescription("商品id");
        paramCreateParamResp.setExample("22");
        paramCreateParamResp.setParentId("");
        // 类型参数，关联字典
        DocParamResp paramCreateParamResp2 = new DocParamResp();
        paramCreateParamResp2.setName("type");
        paramCreateParamResp2.setType("enum");
        paramCreateParamResp2.setDescription("产品类别");
        paramCreateParamResp2.setExample("PHONE");
        paramCreateParamResp2.setParentId("");
        // 设置字典
        paramCreateParamResp2.setEnumInfo(getEnumInfoParam());
        item.setResponseParams(Arrays.asList(paramCreateParamResp, paramCreateParamResp2));

        /* 设置错误码 */
        DocParamCode code = new DocParamCode();
        code.setCode("10001");
        code.setMsg("token错误");
        code.setSolution("请传token");
        item.setErrorCodeParams(Arrays.asList(code));

        return item;
    }

    private static EnumInfoParam getEnumInfoParam() {
        EnumInfoParam enumInfoParam = new EnumInfoParam();
        enumInfoParam.setName("产品类别");
        EnumItemParam phone = new EnumItemParam();
        phone.setName("PHONE");
        phone.setType("byte");
        phone.setValue("1");
        phone.setDescription("手机");

        EnumItemParam book = new EnumItemParam();
        book.setName("BOOK");
        book.setType("byte");
        book.setValue("2");
        book.setDescription("图书");

        enumInfoParam.setItems(Arrays.asList(phone, book));
        return enumInfoParam;
    }

    private static List<DocParamReq> buildChildren() {
        List<DocParamReq> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DocParamReq paramCreateParamReq2_child = new DocParamReq();
            paramCreateParamReq2_child.setName("price" + i);
            paramCreateParamReq2_child.setType("int");
            paramCreateParamReq2_child.setDescription("价格");
            paramCreateParamReq2_child.setExample("100");
            paramCreateParamReq2_child.setMaxLength("64");
            paramCreateParamReq2_child.setRequired(Booleans.TRUE);
            list.add(paramCreateParamReq2_child);
        }
        return list;
    }

}
