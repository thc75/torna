# OpenAPI

## 什么是OpenAPI

Torna提供了一组开放接口，第三方服务可以通过开放接口来创建文档，也就是说通过接口调用，把第三方文档推送到Torna中来。

<img src="./static/openapi/images/openapi1.png" style="height: 250px" title="OpenAPI" />

## Java语言

使用Torna自带的sdk调用接口（要求Java7+）。

- pom.xml添加依赖

```xml

<dependency>
  <groupId>cn.torna</groupId>
  <artifactId>torna-sdk</artifactId>
  <version>最新版本</version>
</dependency>
```

`torna-sdk`最新版本：![maven](https://img.shields.io/maven-central/v/cn.torna/torna-sdk)

- 调用示例

```java
/**
 * @author tanghc
 */
public class DocPushTest {
    
    static String url = "http://localhost:7700/api";
    static String token = "78488946f9494242bb079f3acba907a6";
    // 推送客户端
    static OpenClient client = new OpenClient(url);

   /**
   * 推送接口文档
   */
  @Test
    public void testDocPush() {
        DocPushRequest request = new DocPushRequest(token);
        // 创建分类
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        folder.setName("手机分类2");
        folder.setAuthor("李四");

        List<DocItem> items = new ArrayList<>(8);
        // 分类下面有文档
        folder.setItems(items);

        // 创建三个文档
        for (int i = 0; i < 3; i++) {
            DocItem docItem = buildDocItem(i);
            if (i == 1) {
                docItem.setDeprecated("该接口已废弃");
            }
            items.add(docItem);
        }
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
            enumItemParam.setCode("name" + i);
            enumItemParam.setMsg("描述" + i);
//            enumItemParam.setSolution("解决方案" + i);
            errorCodes.add(enumItemParam);
        }
        return errorCodes;
    }

    private static DocItem buildDocItem(int i) {
        DocItem item = new DocItem();
        if (i % 2 == 0) {
            item.setAuthor("张三");
        } else {
            item.setAuthor("王五");
        }
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

        /* 设置header */
        DocParamHeader header = new DocParamHeader();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("请求token");
        header.setExample("xxxx");
        item.setHeaderParams(Arrays.asList(header));

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
```

## 其它语言封装接口

OpenAPI定义了4个公共参数，用json接收

```
{
	"name":"doc.push",
	"version":"1.0",
	"data":"%7B%22goods_name%22%3A%22iphone6%22%7D",	
	"access_token":"ccc",
}
```

- name：接口名称，参见各接口文档
- version：接口版本号，参见各接口文档
- data：业务参数，见每个文档，json格式并且urlencode
- access_token：项目模块对应的token

## 请求示例

```java
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PostTest extends TestCase {

  static String url = "http://localhost:7700/api";
  static String token = "e807db2eb8564c4b89caf5a2f2d15b77";

  @Test
  public void testPost() throws IOException {
    // 业务参数
    Map<String, Object> bizParams = new HashMap<String, Object>();
    bizParams.put("id", "9VXEyXvg");

    String json = JSON.toJSONString(bizParams);
    json = URLEncoder.encode(json, "utf-8");

    // 公共参数
    Map<String, Object> param = new HashMap<String, Object>();
    // 设置接口名
    param.put("name", "doc.get");
    // 设置业务参数
    param.put("data", json);
    // 设置版本号
    param.put("version", "1.0");
    // 设置token
    param.put("access_token", token);

    System.out.println("=====请求数据=====");
    String postJson = JSON.toJSONString(param);
    System.out.println(postJson);
    // 发送请求
    String resp = HttpUtil.postJson(url, postJson);
  }

}
```

