## 脚本

脚本分为两类：`前置脚本`和`后置脚本`

### 前置脚本

请求前预处理，可动态修改请求参数等信息。

#### 内置参数

脚本提供了一些内置参数，可以直接使用。

- `req`对象

存放请求信息，具体内容如下：

| 参数 | 类型 | 说明 |
| ---- | ---- | ---- |
| req.url | 字符串 | 请求URL，字符串格式 |
| req.method | 字符串 | 请求方法，如：GET |
| req.params | object | 请求的query参数 |
| req.data | object | 请求的body体参数 |
| req.headers | object | 请求的header |

 
可通过`console.log(req)`查看详情

- `lib`对象

存放常用的类库，目前包括：

| 参数  | 类型 | 说明                                                                                                                |
| ---- | ---- |-------------------------------------------------------------------------------------------------------------------|
| lib.axios | Object | axios HTTP库                                                                                                       |
| lib.CryptoJS | Object | 基于`crypto-js（4.0.0）`加密库，可进行`MD5/AES/sha1/sha256`等加密操作，API使用方式参考：[CryptoJS API](https://cryptojs.gitbook.io/docs/) |
| lib.moment | Object | 时间处理库（2.27.0），[docs](https://momentjs.com/docs/)                                                                  |
| lib.qs | Object | querystring库（6.10.1）                                                                                              |
| lib.RSA | Object | 自定义的RSA签名库，使用方法见文末                                                                                                |
| lib.loadJs | Function | 加载第三方js，使用方法见文末                                                                                                   |


示例：

```javascript
const CryptoJS = lib.CryptoJS;
// Encrypt
var ciphertext = CryptoJS.AES.encrypt('my message', 'secret key 123').toString();
 
// Decrypt
var bytes  = CryptoJS.AES.decrypt(ciphertext, 'secret key 123');
var originalText = bytes.toString(CryptoJS.enc.Utf8);
 
console.log(originalText); // 'my message'
```

- `sys`对象

| 参数       | 类型       | 说明                                                     |
|----------|----------|--------------------------------------------------------|
| sys.send | Function | 调用`sys.send()`手动触发请求，比如请求前获取token，拿到token再手动sys.send() |

```javascript

...做一些前置动作，比如获取token

// 手动调用发送请求
sys.send()

// 必须返回true
return true;
```

#### 前置脚本示例

- 将密码进行MD5

假设登录请求json如下：

```json
{
    "username": "jim",
    "password": "123"
}
```

需要对密码进行MD5，编写脚本如下：

```javascript
// 查看req内容
console.log(req)
// 获取请求body
var data = req.data
var password = data.password
// 将密码进行MD5
var CryptoJS = lib.CryptoJS

var hash = CryptoJS.MD5(password)
data.password = hash.toString(CryptoJS.enc.Hex)
console.log('data', data)
```

- 请求接口前先获取token

有时候请求后端接口需要token，可以在发送之前先获取token，放在header中，然后再发送请求

方式1：返回Promise对象

脚本实例：


```javascript
var loginUrl = 'http://127.0.0.1:8083/login/login'
var data = {
    username: "admin",
    password: "123456"
}
var TOKEN_KEY ='xx-token'

// 判断本地有没有
var token = localStorage.getItem(TOKEN_KEY)
if (token) {
    console.log('本地已经有token:' + token)
    // 放入内置对象headers中
    req.headers.token = token
    // 什么都不做，返回
    return;
}
return new Promise((resolve, reject) => {
    // 请求后端接口获取token
    // 使用lib内置对象获取axios
    lib.axios({
        url: loginUrl,
        method: 'post',
        data
    }).then(res => {
        /*
        假设服务端返回：
        {
            "code": 0,
            "data": {
                "token": "xxxx"
            }
        }
         */
        // 拿到服务端返回的token
        var token = res.data.data.token
        console.log('返回token', token)
        // 保存在本地
        localStorage.setItem(TOKEN_KEY, token)
        // 放入内置对象headers中
        req.headers.token = token

        // 塞回内置对象
        resolve(req)
    }).catch(error => {
        console.log(error)
        reject(error)
    })
})
```

最后必须返回Promise对象

方式2：拿到token后手动调用`sys.send()`进行请求

```javascript
var loginUrl = 'http://127.0.0.1:8083/login/login'
var data = {
    username: "admin",
    password: "123456"
}
var TOKEN_KEY ='xx-token'
// 判断本地有没有
var token = localStorage.getItem(TOKEN_KEY)
if (token) {
    console.log('本地已经有token:' + token)
    // 放入内置对象headers中
    req.headers.token = token
    // 什么都不做，返回
    return;
}
// 请求后端接口获取token
// 使用lib内置对象获取axios
lib.axios({
    url: loginUrl,
    method: 'post',
    data
}).then(res => {
    /*
       假设服务端返回：
        {
            "code": 0,
            "data": {
                "token": "xxxx"
            }
        }
        */
    // 拿到服务端返回的token
    var token = res.data.data.token
    console.log('返回token:', token)
    // 保存在本地
    localStorage.setItem(TOKEN_KEY, token)
    // 放入内置对象headers中
    req.headers.token = token

    // 手动调用发送请求
    sys.send()

}).catch(error => {
    console.log(error)
})

// 如果手动调用必须显式返回true
return true
```

最后必须返回true，表示手动调用请求

- 复杂示例：将参数进行签名

开放平台需要对参数进行签名，此处演示使用RSA算法生成签名

```javascript
/*
演示签名认证
*/
// APP_ID
var appId = '2019032617262200001';
// 私钥内容
var privateKey = 'xx'
// 业务参数
var bizContent = req.data

// 先按name进行排序，然后按name1=value1&name2=value2方式连接起来
function getSignContent(params) {
    const paramNames = [];
    for (const key in params) {
        paramNames.push(key);
    }
    paramNames.sort();
    const paramNameValue = [];
    for (let i = 0, len = paramNames.length; i < len; i++) {
        const paramName = paramNames[i];
        const val = params[paramName];
        if (paramName && val) {
            paramNameValue.push(`${paramName}=${val}`);
        }
    }
    return paramNameValue.join('&');
}

const allParams = {
    'app_id': appId,
    'method': req.headers.method,
    'charset': 'UTF-8',
    'sign_type': 'RSA2',
    'timestamp': lib.moment().format('YYYY-MM-DD HH:mm:ss'),
    'version': req.headers.version,
    'biz_content': JSON.stringify(bizContent)
};
// 生成签名内容
var signContent = getSignContent(allParams);
// 生成签名，RSA签名，hash方式采用：SHA256withRSA
var sign = lib.RSA.signToB64(signContent, privateKey, lib.RSA.HASH_TYPE.SHA256withRSA)
// 将签名串添加到系统参数中
allParams.sign = sign
// 替换请求参数
req.data = allParams
```

### 后置脚本

请求后处理结果，可动态修改返回内容。

#### 内置参数

- `req`对象

内容同上

- `resp`对象：存放了返回信息

| 参数 | 类型 | 说明                |
| ---- | ---- |-------------------|
| resp.status | number | http status，如：200 |
| resp.data | object | 返回内容              |
| resp.headers | object | 服务器响应头            |


可通过`console.log(resp)`查看更多内容

- `lib`对象

内容同上

#### 后置脚本示例

假设登录成功返回结果如下：

```json
{
    "code": 0,
    "data": {
        "userId": 11,
        "username": "Jim",
        "token": "xxx"
    }
}
```

现在把token存到本地

```javascript
// 获取返回数据
var body = resp.data
if (body.code == 0) {
  var token = body.data.token
  // 保存token到本地
  localStorage.setItem('xx-token', token)
}
```

## lib.RSA使用

```javascript
/**
 * rsa签名
 * @param content {string}  签名内容
 * @param privateKey {string} 私钥
 * @param hash {string} hash算法，SHA256withRSA，SHA1withRSA
 * @returns {string} 返回签名字符串，base64后的内容
 */
var sign = lib.RSA.signToB64(content, privateKey, 'SHA256withRSA');
```

## 加载第三方js

```javascript
/**
 * 加载JS
 * @param url js全路径
 * @param success 加载成功后回调函数
 */
function loadJs(url, success){}
```

使用：

```javascript
// 加载js文件
lib.loadJs('http://libs.baidu.com/jquery/2.0.0/jquery.min.js', function() {
    console.log(jQuery('#app'))
})
```
