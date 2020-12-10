# 如何请求接口

## Java语言

使用Torna自带的sdk调用接口。

- pom.xml添加依赖

```xml
<dependency>
  <groupId>net.oschina.durcframework</groupId>
  <artifactId>torna-sdk</artifactId>
  <version>0.0.1</version>
</dependency>
```

- 调用示例

```java
static String url = "http://localhost:7700/api";
static String appKey = "20201127781912960996999168";
static String secret = "ltatugCHeRJzCvjVxF39A%6.F$eV#~~L";
static String token = "e807db2eb8564c4b89caf5a2f2d15b77";

// 创建请求客户端
static OpenClient client = new OpenClient(url, appKey, secret);

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

OpenAPI定义了6个公共参数，用json接收

```
{
	"name":"goods.get",
	"version":"2.0",
	"app_key":"test",
	"data":"%7B%22goods_name%22%3A%22iphone6%22%7D",	
	"timestamp":"2018-01-16 17:02:02",
	"sign":"4CB446DF67DB3637500D4715298CE4A3"
}
```

- name:接口名称
- version：接口版本号
- app_key：分配给客户端的app_key
- data：业务参数，见每个文档，json格式并且urlencode
- timestamp：时间戳，yyyy-MM-dd HH:mm:ss
- sign：签名串

其中`sign`需要使用双方约定的签名算法来生成。

签名算法描述如下：

1. 将请求参数按参数名升序排序；
2. 按请求参数名及参数值(不能为空)相互连接组成一个字符串：`<paramName1><paramValue1><paramName2><paramValue2>...`
3. 将应用密钥分别添加到以上请求参数串的头部和尾部：`<secret><请求参数字符串><secret>`
4. 对该字符串进行MD5（全部大写），MD5后的字符串即是这些请求参数对应的签名；
5. 该签名值使用sign参数一起和其它请求参数一起发送给服务开放平台。

伪代码:

```java
Map<String,Object> paramsMap = new ...; // 参数

Set<String> keySet = paramsMap.keySet();
List<String> paramNames = new ArrayList<String>(keySet);
// 1. 将请求参数按参数名升序排序；
Collections.sort(paramNames);

StringBuilder paramNameValue = new StringBuilder();
// 2. 按请求参数名及参数值相互连接组成一个字符串：`<paramName1><paramValue1><paramName2><paramValue2>...`
for (String paramName : paramNames) {
    Object value = paramsMap.get(paramName);
    if (value != null) {
        paramNameValue.append(paramName).append(value);
    }
}
// 3. 将应用密钥分别添加到以上请求参数串的头部和尾部：`<secret><请求参数字符串><secret>`
String source = secret + paramNameValue.toString() + secret;
// 4. 对该字符串进行MD5（全部大写），MD5后的字符串即是这些请求参数对应的签名；
String sign = md5(source);
// 5. 该签名值使用sign参数一起和其它请求参数一起发送给服务开放平台。
paramsMap.put("sign",sign);
```

## 请求示例

```java
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

import junit.framework.TestCase;

public class PostTest extends TestCase {

    @Test
    public void testPost() throws IOException {
        String appKey = "test";
        String secret = "123456";
        // 业务参数
        Map<String, String> bizParams = new HashMap<String, String>();
        bizParams.put("goodsName", "iphoneX");

        String json = JSON.toJSONString(bizParams);
        json = URLEncoder.encode(json, "utf-8");

        // 公共参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", "goods.get");
        param.put("app_key", appKey);
        param.put("data", json);
        param.put("timestamp", getTime());
        param.put("version", "1.0");
        param.put("access_token", "");

        String sign = buildSign(param, secret);

        param.put("sign", sign);

        /*
        // 最终请求数据
        {
            "sign": "2AE534A15AACE112EE43B9CCF6BD4383",
            "timestamp": "2018-03-21 12:57:30",
            "name": "goods.get",
            "data": "%7B%22goodsName%22%3A%22iphoneX%22%7D",
            "app_key": "test",
            "version": "1.0"
        }
        */
        System.out.println("=====请求数据=====");
        String postJson = JSON.toJSONString(param);
        System.out.println(postJson);
        // String resp = HttpUtil.post(postJson); // 发送请求
        /*
        响应结果:
        {
            "code":"0",
            "data":{
                "pageIndex":1,
                "pageSize":10,
                "rows":[
                    {
	                    "goods_name":"iPhoneX",
	                    "id":1,
	                    "price":8000
                    },
                    {
	                    "goods_name":"三星",
	                    "id":2,
	                    "price":7000
                    }
	           ],
	           "total":100
            }
        }
        */
    }

    /**
     * 构建签名
     *
     * @param paramsMap
     *            参数
     * @param secret
     *            密钥
     * @return
     * @throws IOException
     */
    public static String buildSign(Map<String, ?> paramsMap, String secret) throws IOException {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList<String>(keySet);

        Collections.sort(paramNames);

        StringBuilder paramNameValue = new StringBuilder();

        for (String paramName : paramNames) {
            Object value = paramsMap.get(paramName);
            if (value != null) {
                paramNameValue.append(paramName).append(value);
            }
        }

        String source = secret + paramNameValue.toString() + secret;

        return md5(source);
    }

    /**
     * 生成md5,全部大写
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2 将消息变成byte数组
            byte[] input = message.getBytes();

            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            return byte2hex(buff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
```