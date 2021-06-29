插件的作用是将本地项目中的Swagger文档内容推送到Torna服务器。[参考文档](http://torna.cn/dev/swagger-plugin.html)

**使用步骤：**

前提：新建一个模块

- SpringBoot项目

pom.xml添加依赖：

```xml
<dependency>
    <groupId>cn.torna</groupId>
    <artifactId>swagger-plugin-starter</artifactId>
    <version>最新版本</version>
    <scope>provided</scope>
</dependency>
```

`swagger-plugin-starter`最新版本：![maven](https://img.shields.io/maven-central/v/cn.torna/swagger-plugin-starter)

`src/main/resources`下添加一个`torna.json`文件，内容如下：

```json
{
  // 开启推送
  "enable": true,
  // 扫描package，多个用": "","隔开。不指定扫描全部
  //"basePackage": "cn.torna.tornaexample.controller.order.OrderDetailController",
  // 推送URL
  "url": "http://localhost:7700/api",
  // appKey
  "appKey": "20201216788835306945118208",
  // secret
  "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR",
  // 模块token
  "token": "931167d9347e4aec9409f2b275437431",

  // 调试环境，格式：环境名称,调试路径，多个用"|"隔开
  "debugEnv": "test,http://127.0.0.1:8088",
  // 推送人
  "author": "Jim",
  // 打开调试:true/false
  "debug": true,
  // 是否替换文档，true：替换，false：不替换（追加）。默认：true
  "isReplace": true
}
```

启动项目，插件会自动把swagger文档推送到Torna服务器。

- SpringMVC项目

pom.xml添加依赖：

```xml
<dependency>
    <groupId>cn.torna</groupId>
    <artifactId>swagger-plugin</artifactId>
    <version>最新版本</version>
</dependency>
```

`swagger-plugin`最新版本： ![maven](https://img.shields.io/maven-central/v/cn.torna/swagger-plugin)


新建一个类，继承`SwaggerPluginConfiguration`

```java
@Configuration
public class SwaggerPluginConfig extends SwaggerPluginConfiguration {
}
```

添加上面的配置

启动项目，插件会自动把swagger文档推送到Torna服务器。