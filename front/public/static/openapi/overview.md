# OpenAPI

## 什么是OpenAPI

Torna提供了一组开放接口，第三方服务可以通过开放接口来创建文档，也就是说通过接口调用，把第三方文档推送到Torna中来。

<img src="/static/openapi/images/openapi1.png" style="height: 250px" title="OpenAPI" />

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
static String url = "http://localhost:7700/api";
static String token = "e807db2eb8564c4b89caf5a2f2d15b77";

// 创建请求客户端
static OpenClient client = new OpenClient(url);

/**
 * 获取文档信息
 */
public void testDocGetRequest() {
    // 创建请求对象
    DocGetRequest request = new DocGetRequest(token);
    // 设置请求参数
    request.setId("9VXEyXvg");

    // 发送请求
    DocGetResponse response = client.execute(request);
    if (response.isSuccess()) {
        // 返回结果
        DocDetailResult data = response.getData();
        System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
    } else {
        System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
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
import org.junit.Test;

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

