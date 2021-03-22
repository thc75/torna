# Mock接口

在后端没有提供接口数据的情况下，前端开发人员可以配置Mock，模拟返回数据。

Mock地址格式为：`http://ip:port/mock/<文档id>`

点击`添加配置`，填入基本信息，如下图所示：

<img src="/static/help/images/mock1.png" style="height: 300px" />

- 名称：配置名称
- 条件参数：添加一个请求参数，用来区分对应的Mock配置，比如配置A的请求参数为：`type=1`，配置B的参数为：`type=2`

接着填写返回信息

<img src="/static/help/images/mock2.png" style="height: 450px" />

- Http Status：响应状态值
- 延迟响应：单位毫秒，延迟多少毫秒返回数据，用来模拟服务端耗时操作
- 响应Header：响应头，如果过返回json数据，建议添加`Content-Type=application/json;charset=UTF-8`
- 响应内容：期望返回的结果

设置后使用postman请求结果如下：

<img src="/static/help/images/mock3.png" style="height: 500px" />

## Mock脚本

Mock脚本基于 [mockjs](http://mockjs.com/) ，可书写js代码，[示例](http://mockjs.com/examples.html)

- 方式1：单行脚本

在编辑器中填入下面这个脚本

```javascript
Mock.mock({
  "object|2-4": {
    "110000": "北京市",
    "120000": "天津市",
    "130000": "河北省",
    "140000": "山西省"
  }
})
```

<img src="/static/help/images/mock4.png" style="height: 250px" />

可点击`运行`调试是否可行，没有问题后，点击保存，然后使用postman请求结果如下：

<img src="/static/help/images/mock5.png" style="height: 400px" />

再来一个复杂点的

```javascript
Mock.mock({
  // 20条数据
  "data|20": [{
    // 商品种类
    "goodsClass": "女装",
    // 商品Id
    "goodsId|+1": 1,
    // 商品名称
    "goodsName": "@ctitle(10)",
    // 商品地址
    "goodsAddress": "@county(true)",
    // 商品等级评价★
    "goodsStar|1-5": "★",
    // 商品图片
    "goodsImg": "@Image('100x100','@color','小甜甜')",
    // 商品售价
    "goodsSale|30-500": 30
  }]
})
```

运行结果：

```json
{
    "data": [
        {
            "goodsClass": "女装",
            "goodsId": 1,
            "goodsName": "济因办才群到什装表产",
            "goodsAddress": "山西省 运城市 河津市",
            "goodsStar": "★",
            "goodsImg": "http://dummyimage.com/100x100/f28479&text=小甜甜",
            "goodsSale": 314
        },
        {
            "goodsClass": "女装",
            "goodsId": 2,
            "goodsName": "可广需就厂活相几线经",
            "goodsAddress": "福建省 福州市 长乐市",
            "goodsStar": "★★★★",
            "goodsImg": "http://dummyimage.com/100x100/7991f2&text=小甜甜",
            "goodsSale": 475
        },
        {
            "goodsClass": "女装",
            "goodsId": 3,
            "goodsName": "上必劳干新理务千地元",
            "goodsAddress": "辽宁省 本溪市 平山区",
            "goodsStar": "★★",
            "goodsImg": "http://dummyimage.com/100x100/b4f279&text=小甜甜",
            "goodsSale": 74
        }
        ...
    ]
}
```

- 方式2：编写函数

可以编写函数，然后运行函数生成数据，如编写如下脚本

```javascript
function getItems() {
    return Mock.mock({
      "items|4-10": [
        { "id": 2, "label": "华为" }
      ]
    })
}

// 必须要有一个主函数
function main() {
    var result = {
        "id": 1,
        "name": "分类"
    }
    var items = getItems()
    Object.assign(result, items)
    return result;
}
// 最后一行执行并返回
main();
```

<img src="/static/help/images/mock6.png" style="height: 400px" />

postman请求结果如下：

<img src="/static/help/images/mock7.png" style="height: 400px" />

**注意**：在编写函数的情况下，最后一行必须执行一个主函数，然后返回数据

更多方式：


```javascript
// 必须要有一个主函数
function main() {
    var arr = [1,1,1,1,1,1,1,1]
    var result = {
        "id": 1,
        "name": "省份",
        "items": arr.map(item => {
            return Mock.mock('@province')
        })
    }
    return result;
}
// 最后一行执行并返回
main();
```

返回：

```json
{
    "id": 1,
    "name": "省份",
    "items": [
        "江西省",
        "上海",
        "湖北省",
        "台湾",
        "内蒙古自治区",
        "湖北省",
        "西藏自治区",
        "青海省"
    ]
}
```

> 备注：Mock脚本是预先生成结果然后保存到数据库中等待请求，因此每次请求的结果都是一样的。
