# swagger插件

## 介绍

插件的作用是将本地项目中的Swagger文档内容推送到Torna服务器。

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

`application.properties`添加：

```properties
# 开启推送
torna.swagger-plugin.enable=true
# 扫描package，多个用";"隔开。不指定扫描全部
#torna.swagger-plugin.basePackage=com.example.xx.controller
# 推送URL，见：项目首页->OpenAPI
torna.swagger-plugin.url=http://localhost:7700/api
# 模块token，见：项目首页->OpenAPI
torna.swagger-plugin.token=e65623dde76d4b4e8fbdfd8591e43145
# appKey，见：空间->开放用户
torna.swagger-plugin.app-key=20201216788835536872669184
# secret，见：空间->开放用户
torna.swagger-plugin.secret=Bq.XRN!S0$t8!UYpWgSOl7oHlY#XeenJ
# 调试环境，格式：环境名称,调试路径，多个用"|"隔开
# 如：local,http://127.0.0.1:2222|test,http://10.0.10.11:2222
torna.swagger-plugin.debug-env=test,http://127.0.0.1:2222
# 推送人
torna.swagger-plugin.author=Jim
# 打开调试:true/false
torna.swagger-plugin.debug=true
# 接口多个method只显示
torna.swagger-plugin.method-when-multi=GET
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

新建一个类，继承`SwaggerPluginConfiguration`

```java
@Configuration
public class SwaggerPluginConfig extends SwaggerPluginConfiguration {
}
```

添加上面的配置

启动项目，插件会自动把swagger文档推送到Torna服务器。