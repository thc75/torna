# 全局参数

全局参数，也叫公共参数，分为`全局请求参数`和`全局返回参数`

## 全局请求参数

设置了全局请求参数，每个接口请求都要带上这个参数，设置方式：前往`模块配置` -> `全局请求参数`进行配置

然后文档浏览页，请求参数列表中都会出现配置的全局请求参数。

## 全局返回参数

全局返回参数配置方式同全局请求参数一样，只不过这里多了数据节点。

- 为何要数据节点？

假如您的项目使用AOP技术做到统一结果返回，如：

```json
{
    "code": "1000",
    "msg": "success",
    "data": {...}
}
```

其中，code, msg, data部分属于全局返回参数，`{...}`属于业务参数。

假设controller只返回业务结果

```java
@RequestMapping("/findAll")
public List<User> findAll(){
    return userService.list();
}
```

这样只能定义业务返回参数，外层的全局参数很难定义（当然也有解决办法）。

在这种情况下，可以配置全局返回参数，然后指定数据节点，具体操作方式如下：

前往`模块配置` -> `全局返回参数`，点击`添加`

<img src="/static/help/images/global1.png" style="height: 300px" />

填写表单

<img src="/static/help/images/global2.png" style="height: 500px" />

这里顺便指定了关联枚举，用来解释各个code值，可以事先前往`字典管理`进行配置

<img src="/static/help/images/global3.png" style="height: 500px" />

接着使用同样的方式添加`msg`字段。

最后添加`data`字段，因为data字段是数据节点，需要设置为数据节点。

<img src="/static/help/images/global4.png" style="height: 500px" />

前往文档浏览页，展示结果如下：

<img src="/static/help/images/global5.png" style="height: 350px" />

## 不使用全局参数

如果指定了全局参数（`全局请求头/全局请求参数/全局返回参数`）默认情况下每个接口都会使用，但是有些特殊接口又不需要使用。

前往文档编辑页面进行设置，以`全局请求参数`为例，进入文档编辑页面，取消勾选`使用全局请求参数`，这样该文档就不会使用`全局请求参数`。

<img src="/static/help/images/global6.png" style="height: 350px" />

`全局header`和`全局返回参数`设置方式与此类似。