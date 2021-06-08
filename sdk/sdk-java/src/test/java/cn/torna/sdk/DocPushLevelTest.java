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
import java.util.List;

/**
 * 多级文件夹推送
 * @author tanghc
 */
public class DocPushLevelTest extends BaseTest {

    public void testDocPush() {
        DocPushRequest request = new DocPushRequest(token);

        // 设置请求参数
        DocItem folderRoot1 = getRoot("大类1");
        DocItem folderRoot2 = getRoot("大类2");
        // 创建调试环境
        DebugEnv debugEnv = new DebugEnv("测试环境", "http://localhost:8090");
        request.setApis(Arrays.asList(folderRoot1, folderRoot2));
        request.setDebugEnvs(Arrays.asList(debugEnv));

        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("请求成功");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    private DocItem getRoot(String rootName) {
        DocItem folderRoot = new DocItem();
        folderRoot.setIsFolder(Booleans.TRUE);
        folderRoot.setName(rootName);

        // 创建分类
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        folder.setName("小类1");

        DocItem folder2 = new DocItem();
        folder2.setIsFolder(Booleans.TRUE);
        folder2.setName("小类2");
        folder2.setItems(Arrays.asList(buildDocItem(99)));

        // 父文件夹下面有两个子文件夹，加一个文档
        DocItem subDoc = buildDocItem(88);
        folderRoot.setItems(Arrays.asList(folder, folder2, subDoc));

        List<DocItem> items = new ArrayList<>(8);
        // 分类下面有文档
        folder.setItems(items);

        // 创建三个文档
        for (int i = 1; i < 4; i++) {
            DocItem docItem = buildDocItem(i);
            items.add(docItem);
        }
        return folderRoot;
    }

    private static DocItem buildDocItem(int i) {
        DocItem item = new DocItem();
        /* 设置基本信息 */
        item.setName("获取商品名称" + i);
        item.setDescription("这里是描述信息..." + i);
        item.setUrl("/goods/{id}/get" + i);
        item.setHttpMethod("GET");
        item.setContentType("application/json");
        item.setParentId("");
        item.setIsShow(Booleans.TRUE);

        /* 设置path参数 */
        DocParamPath pathParam = new DocParamPath();
        pathParam.setName("id");
        pathParam.setType("int");
        pathParam.setDescription("id");
        pathParam.setExample("123");
        pathParam.setMaxLength("-");
        pathParam.setRequired(Booleans.TRUE);
        item.setPathParams(Arrays.asList(pathParam));

        /* 设置Query参数 */
        DocParamReq queryCreateParamReq = new DocParamReq();
        queryCreateParamReq.setName("uid");
        queryCreateParamReq.setType("number");
        queryCreateParamReq.setDescription("uid");
        queryCreateParamReq.setExample("1111");
        queryCreateParamReq.setMaxLength("64");
        queryCreateParamReq.setRequired(Booleans.TRUE);
        queryCreateParamReq.setParentId("");
        item.setQueryParams(Arrays.asList(queryCreateParamReq));

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
