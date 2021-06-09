# 调试接口

如果要调试接口，必须先设置调试环境。前往【模块配置】-> 【调试环境】

<img src="/static/help/images/debug1.png" style="height: 300px" />

点击`新增环境`，输入环境名称以及调试路径，到端口号即可

<img src="/static/help/images/debug3.png" style="height: 300px" />

切换到【浏览模式】预览文档，在调试页面会出现刚才配置的调试环境

<img src="/static/help/images/debug4.png" style="height: 200px" />

此外还可以添加测试环境等其它环境，方便在不同环境中进行调试。

## 脚本

脚本分为两类：`Pre-request Script`和`After Response Script`

### Pre-request Script

请求前预处理，可动态修改请求参数等信息。

#### 内置参数

脚本提供了一些内置参数，可以拿来直接使用。

- `ctx`对象

存放请求信息，具体内容如下：

| 参数 | 类型 | 说明 |
| ---- | ---- | ---- |
| ctx.url | 字符串 | 请求URL，字符串格式 |
| ctx.method | 字符串 | 请求方法，如：GET |
| ctx.params | object | 请求的query参数 |
| ctx.data | object | 请求的body体参数 |
| ctx.headers | object | 请求的header |

 
可通过`console.log(ctx)`查看详情

- `CryptoJS`对象

基于`crypto-js`加密库，可进行`MD5/AES/sha1/sha256`等加密操作，API使用方式参考：[CryptoJS API](https://cryptojs.gitbook.io/docs/)

示例：

```javascript
// Encrypt
var ciphertext = CryptoJS.AES.encrypt('my message', 'secret key 123').toString();
 
// Decrypt
var bytes  = CryptoJS.AES.decrypt(ciphertext, 'secret key 123');
var originalText = bytes.toString(CryptoJS.enc.Utf8);
 
console.log(originalText); // 'my message'
```

脚本示例：

> 将密码进行MD5

假设请求json如下：

```json
{
    "username": "jim",
    "password": "123"
}
```

```javascript
// 查看ctx内容
console.log(ctx)
// 获取请求body
var data = ctx.data
var password = data.password
// 将密码进行MD5
var hash = CryptoJS.MD5(password)
data.password = hash.toString(CryptoJS.enc.Hex)
console.log('data', data)
```

### After Response Script

请求后处理结果，可动态修改返回内容。

#### 内置参数

- resp：存放了返回信息

| 参数 | 类型 | 说明 |
| ---- | ---- | ---- |
| resp.status | number | http status，如：200 |
| resp.data | object | 返回内容 |
| resp.targetHeaders | object | 返回的请求头 |


`resp`实际上是axios返回的对象，可通过`console.log(resp)`查看更多内容

- `CryptoJS`对象

内容同上

脚本示例：

假设返回结果如下：

```json
{
    "code": 0,
    "data": {
        "userId": 11,
        "orderNo": "1",
        "payTime": "2021-06-09T07:28:50.941+00:00",
        "remark": null
    }
}
```

```javascript
var params = ctx.params
// 如果id为1，将用户id改成666
if (params.id == 1) {
    // 获取返回数据
    var body = resp.data
    body.data.userId = 666;
}
```
