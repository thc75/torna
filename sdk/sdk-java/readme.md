# torna-sdk-java

开放平台对应的sdk

## 接口封装步骤

比如获取商品信息接口

- 接口名：goods.get
- 版本号：""(空字符串)
- 参数：goods_name 商品名称
- 返回信息

```
{
    "code":"0",
    "data":{
        "goods_name":"苹果iPhoneX",
        "id":1,
        "price":8000
    }
}
```

针对这个接口，封装步骤如下：



1.在`result`包下新建一个类来接收`data`部分

字段统一使用小写字母+下划线形式，如:name,user_age

```
public class Goods {

    private Long id;
    private String goods_name;
    private BigDecimal price;

    省略 get set
}
```

2.在`response`包下新建一个返回类，继承`BaseResponse`

BaseResponse中有个泛型参数，填`Goods`类，表示返回的数据对象。

```
public class GetGoodsResponse extends BaseResponse<Goods> {
}
```

3.在`request`包下新建一个请求类，继承`BaseRequest`

BaseRequest中有个泛型参数，填`GetGoodsResponse`类，表示这个请求对应的返回类。
重写`name()`方法，填接口名。

如果要指定版本号，可重写`version()`方法

在request类中设置请求字段

```
public class GetGoodsRequest extends BaseRequest<GetGoodsResponse> {

    private String goods_name;

    @Override
    public String name() {
        return "goods.get";
    }
}
```

## 使用方式

```
// 创建请求对象
GetGoodsRequest request = new GetGoodsRequest(token);
request.setGoods_name("iphone6");

// 发送请求
GetGoodsResponse response = client.execute(request);

System.out.println("--------------------");
if (response.isSuccess()) {
    // 返回结果
    Goods goods = response.getData();
    System.out.println(goods);
} else {
    System.out.println("errorMsg:" + response.getMsg());
}
System.out.println("--------------------");
```
