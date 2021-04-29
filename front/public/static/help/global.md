# 公共参数

分为`公共请求参数`和`公共返回参数`

下面以`公共请求参数`为例，讲解如何进行设置，前往`模块配置` -> `公共请求参数`

假如您的请求参数有固定格式，如：

```json
{
    "requestInfo": {
      "reqId": "asdfasx",
      "userId": 123123
    },
    "body": { // 数据节点，存放业务参数
     ...
    }
}
```

步骤如下：

- 先添加一个父节点：`requestInfo`，类型 object
- 在`requestInfo`下添加两个子节点，reqId，userId
- 添加一个父节点：`body`，指定数据节点

最终结果如下图所示：

<img src="/static/help/images/global1.png" style="height: 250px" />

文档页面效果如下：

<img src="/static/help/images/global2.png" style="width: 600px" />

公共返回参数配置方式与此类似。


## 不使用全局参数

如果指定了全局参数（`公共请求头/公共请求参数/公共返回参数`）默认情况下每个接口都会使用，但是有些特殊接口又不需要使用。

前往文档编辑页面进行设置，以`公共请求参数`为例，进入文档编辑页面，取消勾选`使用公共请求参数`，这样该文档就不会使用`公共请求参数`。

<img src="/static/help/images/global3.png" style="width: 700px" />

`公共请求头`和`公共返回参数`设置方式与此类似。