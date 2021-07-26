# Mock接口

在后端没有提供接口数据的情况下，前端开发人员可以配置Mock，模拟返回数据。

点击`添加配置`，填入基本信息

- Mock地址：自动生成，可在url后面添加query参数区分不同mock，如`xx/getOrder?id=2`
- 名称：配置名称

接着填写返回信息

<img src="/static/help/images/mock2.png" style="height: 450px" />

- Http Status：响应状态值
- 延迟响应：单位毫秒，延迟多少毫秒返回数据，用来模拟服务端耗时操作
- 响应Header：响应头，如果过返回json数据，建议添加`Content-Type=application/json;charset=UTF-8`
- 响应内容：期望返回的结果

> 注：未设置Content-Type可能会返回乱码

设置后使用postman请求结果如下：

<img src="/static/help/images/mock3.png" style="height: 500px" />

## Mock脚本

Mock脚本基于 [mockjs](http://mockjs.com/) ，可书写js代码，[示例](http://mockjs.com/examples.html)

> 约定：最后一行需要`return`数据

- 例子1：直接返回

```javascript
var data = Mock.mock({
   "object|2-4": {
     "110000": "北京市",
     "120000": "天津市",
     "130000": "河北省",
     "140000": "山西省"
   }
})
// 最后一行返回数据
return data;
```

<img src="/static/help/images/mock4.png" style="height: 250px" />

可点击`运行`调试是否可行，没有问题后，点击保存，然后使用postman请求结果如下：

<img src="/static/help/images/mock5.png" style="height: 400px" />


再来一个复杂点的

```javascript
var data = Mock.mock({
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
// 最终返回数据
return data;
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

- 例子2：编写多个函数

```javascript
function getItems() {
    return Mock.mock({
      "items|4-10": [
        { "id": 2, "label": "手机" }
      ]
    })
}

function getName() {
    return "分类";
}

var data = {
    "id": 1,
    "name": getName()
}
var items = getItems()
Object.assign(data, items)
// 最后一行返回
return data;
```

运行结果：

```json
{
    "id": 1,
    "name": "分类",
    "items": [
        {
            "id": 2,
            "label": "手机"
        },
        {
            "id": 2,
            "label": "手机"
        },
        {
            "id": 2,
            "label": "手机"
        },
        {
            "id": 2,
            "label": "手机"
        }
    ]
}
```

- 例子3：map遍历

```javascript
var arr = [1,1,1,1,1,1,1,1]
var result = {
    "id": 1,
    "name": "省份",
    "items": arr.map(item => {
        return Mock.mock('@province')
    })
}
return result;
```

运行结果：

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

- 例子4: 扩展

```javascript
var random = Mock.Random;

//扩展数据模板
random.extend({
  type: function(index) {
    const types = ['products', 'industryApp', 'solution', 'experts'];
    return this.pick(types[index])
  }
});

// 定义数据类型
const menuSource = [];
menuSource[0] = Mock.mock({
  "type": "@type(0)",
   'data|3-4':[{
     'id|+1': 1,
     name: "@ctitle( 4,6)",
     "childs|5-10": [{
       'id|+1': 1,
       name: "@ctitle(4,6)",
     }]
   }]
});

return menuSource;
```

运行结果：

```json
[
    {
        "type": "products",
        "data": [
            {
                "id": 1,
                "name": "心没积战",
                "childs": [
                    {
                        "id": 1,
                        "name": "决料听国立"
                    }
                ]
            },
            {
                "id": 2,
                "name": "属化政却外",
                "childs": [
                    {
                        "id": 2,
                        "name": "众他易族"
                    },
                    {
                        "id": 3,
                        "name": "结值自别难"
                    }
                ]
            }
        ]
    }
]
```


## 忽略参数

默认情况下通过不同的参数来区分不同的mock配置，如果需要忽略任何参数，可在`application.properties`中指定`torna.mock.ignore-param=true`将忽略任何参数

如：`http://xxx/mock/listOrder`,`http://xxx/mock/listOrder?id=1`将返回同样的内容
