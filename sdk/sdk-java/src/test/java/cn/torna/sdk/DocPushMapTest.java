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
 * @author tanghc
 */
public class DocPushMapTest extends BaseTest {

    /**
     * 针对接收参数为map的情况
     * <pre>
     * {@literal
     *  public void param(Map<String, User> map) {
     *  }
     *  }
     * </pre>
     */
    public void testDocPush() {
        DocPushRequest request = new DocPushRequest(token);
        // 创建分类
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        folder.setName("Map");
        folder.setAuthor("李四");

        List<DocItem> items = new ArrayList<>(8);
        // 分类下面有文档
        folder.setItems(items);

        // 创建文档
        DocItem docItem = buildDocItem();
        items.add(docItem);

        // 创建调试环境
        DebugEnv debugEnv = new DebugEnv("测试环境", "http://localhost:8090");

        // 设置请求参数
        request.setApis(Arrays.asList(folder));
        request.setDebugEnvs(Arrays.asList(debugEnv));
        request.setAuthor("张三");
        request.setCommonErrorCodes(buildEnumItemParamList());
        // 是否替换文档，true：替换，false：不替换（追加）。默认：true
//        request.setIsReplace(Booleans.FALSE);

        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("请求成功");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    private List<DocParamCode> buildEnumItemParamList() {
        List<DocParamCode> errorCodes = new ArrayList<>(3);
        for (int i = 0; i < 4; i++) {
            DocParamCode enumItemParam = new DocParamCode();
            enumItemParam.setCode("name");
            enumItemParam.setMsg("描述");
//            enumItemParam.setSolution("解决方案");
            errorCodes.add(enumItemParam);
        }
        return errorCodes;
    }

    private static DocItem buildDocItem() {
        DocItem item = new DocItem();
        /* 设置基本信息 */
        item.setAuthor("王五");
        item.setName("获取商品名称");
        item.setDescription("这里是描述信息...");
        item.setUrl("/goods/get");
        item.setHttpMethod("POST");
        item.setContentType("application/json");
        item.setParentId("");
        item.setIsShow(Booleans.TRUE);

        /* 设置header */
        DocParamHeader header = new DocParamHeader();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("请求token");
        header.setExample("xxxx");
        item.setHeaderParams(Arrays.asList(header));


        /* 设置请求参数 */
        DocParamReq paramCreateParamReq = new DocParamReq();
        paramCreateParamReq.setName("key");
        paramCreateParamReq.setType("object");
        paramCreateParamReq.setDescription("参数key");
        paramCreateParamReq.setExample("name");
        paramCreateParamReq.setRequired(Booleans.TRUE);
        paramCreateParamReq.setParentId("");
        List<DocParamReq> children = buildChildren();
        paramCreateParamReq.setChildren(children);
        item.setRequestParams(Arrays.asList(paramCreateParamReq));
        item.setIsRequestArray(Booleans.TRUE);


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
        DocParamReq nameParam = DocParamReq.builder()
                .name("id")
                .type("int")
                .description("用户id")
                .example("11")
                .maxLength("64")
                .required(Booleans.TRUE)
                .build();

        DocParamReq nameParam2 = DocParamReq.builder()
                .name("name")
                .type("string")
                .description("用户名称")
                .example("Jim")
                .maxLength("64")
                .required(Booleans.TRUE)
                .build();


        list.add(nameParam);
        list.add(nameParam2);
        return list;
    }

}
