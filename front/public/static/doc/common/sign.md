# 签名算法

签名步骤如下：

1.筛选并排序

获取所有请求参数，不包括字节类型参数，如文件、字节流，剔除sign字段，剔除值为空的参数，并按照参数名ASCII码递增排序（字母升序排序），如果遇到相同字符则按照第二个字符的键值ASCII码递增排序，以此类推。

2.拼接

将排序后的参数与其对应值，组合成“参数=参数值”的格式，并且把这些参数用&字符连接起来，此时生成的字符串为待签名字符串。


3.调用签名函数

使用各自语言对应的`SHA256WithRSA`签名函数并使用应用私钥对待签名字符串进行签名，对结果Base64编码。

4.把生成的签名赋值给`sign`参数，拼接到请求参数中。


- 示例

假设目前已经存在一个接口：获取会员信息

- 接口名：member.info.get
- 版本号：1.0
- 请求方式：GET
- 请求参数：name=jim&age=123&address=xxx

它的业务参数为：

```
name=jim
age=22
address=xx
```

加上公共请求参数：

```
app_id=test_2020050924567817013559296
method=member.info.get
version=1.0
charset=UTF-8
timestamp=2019-06-03 15:18:29
sign=（待生成）
```

把业务请求参数和公共请求参数加起来，然后按照参数名ASCII码递增排序

则待签名字符串为：

```
address=xx&age=22&app_id=test_2020050924567817013559296&charset=UTF-8&method=member.info.get&name=jim&timestamp=2020-06-03 15:23:30&version=1.0
```

使用各自语言对应的`SHA256WithRSA`签名函数并使用`应用私钥`对待签名字符串进行签名，对结果Base64编码，得到字符串：`adfdxadsf3asdfa`

把该字符串给`sign`参数，拼接到请求参数中，得到最终请求参数为：

```
name=jim
age=22
address=xx
app_id=test_2020050924567817013559296
method=member.info.get
version=1.0
charset=UTF-8
timestamp=2019-06-03 15:18:29
sign=adfdxadsf3asdfa
```

如果开放平台已经提供SDK，那么SDK中已经封装好签名步骤，直接调用SDK中的方法即可完成接口请求。
