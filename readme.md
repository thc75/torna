# Torna

企业接口文档解决方案，目标是让文档管理变得更加方便、快捷。Torna采用团队协作的方式管理和维护项目API文档，将不同形式的文档纳入进来，形成一个统一的维护方式。

<img src="./front/public/static/images/arc.png" width="80%" height="80%" />

## 当前版本号

| 模块 | 版本 |
| :----: | :----: |
| Torna | 1.8.1 |
| sdk-java | 1.0.9 |
| swagger-plugin | 1.0.2 |

## 特性介绍

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持分享文档（匿名访问、密码访问）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持导出为markdown格式、html格式
- 支持文档聚合，将不同模块中的文档聚合展示
- 支持多环境接口调试
- 支持Mock数据
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 支持第三方登录接入/OAuth认证登录
- 支持钉钉免密登录
- 支持设置全局请求头/请求参数/响应参数
- 支持中英文切换(i18n)
- 支持docker运行
- 提供swagger插件，快速导入swagger文档
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰
- 部署简单，直接运行脚本启动程序

---

[演示地址](http://demo.torna.cn/) 演示账号：`guest@torna.cn` 密码：`123456`

当前账号是访问者，只能阅读文档，可以自行部署到本地体验更多功能。

## 使用步骤

### 方式1：下载zip本地运行

- 准备工作

  - Java环境，最低要求Java8
  - MySQL，要求5.6.5及以后，5.6.5之前的版本见：[支持低版本MySQL](http://torna.cn/dev/mysql-lower-version.html)

前往 [发行版页面](https://gitee.com/durcframework/torna/releases) ，下载最新版本，解压zip

导入数据库，执行[mysql.sql](./mysql.sql)

打开`application.properties`配置文件，修改数据库连接配置

执行`sh startup.sh`启动（Windows执行`startup.bat`）

访问：`http://ip:7700`

- 后续升级

无特殊说明，只需要覆盖`torna.jar文件`和`dist文件夹`，然后重启即可

### 方式2：docker运行

下载公共镜像

`docker pull tanghc2020/torna:latest`

导入数据库，执行[mysql.sql](./mysql.sql)

复制`server/boot/src/main/resources/application.properties`文件到`/opt/torna/config`下，修改数据库连接配置

执行`docker run --name torna -p 7700:7700 -v /opt/torna/config:/torna/config -d <镜像ID>`

浏览器访问`http://ip:7700`

---

体验账号：

```
密码均为：123456

超级管理员：admin@torna.cn

研发一部空间管理员：dev1admin@torna.cn
研发一部-商城项目（公开）-项目管理员：dev1shop_admin@torna.cn
研发一部-商城项目（公开）-开发者张三：dev1shop_zhangsan@torna.cn
研发一部-访客王五：dev1guest_wangwu@torna.cn


研发二部空间管理员：dev2admin@torna.cn
研发二部-后台项目（私有）-项目管理员：dev2back_admin@torna.cn
研发二部-后台项目（私有）-开发者李四：dev2back_lisi@torna.cn
研发二部-后台项目（私有）-访客：dev2back_guest@torna.cn
研发二部-访客赵六：dev2guest_zhaoliu@torna.cn
```

### docker-compose部署torna
[【docker-compose方式部署torna】](https://gitee.com/durcframework/torna/tree/docker/torna-docker-compose)

### kubernetes部署torna
[【kubernetes部署torna】](https://gitee.com/durcframework/torna/tree/docker/torna-on-kubernetes)

## 推荐组合

**smart-doc + Torna实现文档全流程自动化**

如果您使用Java语言，推荐使用`smart-doc + Torna`

[smart-doc](https://gitee.com/smart-doc-team/smart-doc) + Torna 组成行业领先的文档生成和管理解决方案，使用smart-doc无侵入完成Java源代码和注释提取生成API文档，自动将文档推送到Torna企业级接口文档管理平台。

通过这套组合您可以实现：只需要写完Java注释就能把接口信息推送到Torna平台，从而实现接口预览、接口调试。

推送的内容有：`接口名称/author/Path参数/Header/请求参数/返回参数/字典列表/公共错误码`

如果您是非Java语言，可以使用表单页面编辑以上内容，完成后同样可以进行接口预览、调试。

## 开发部署

参见：[开发文档](http://torna.cn/dev/)

## 更新日志

[changelog](./changelog.md)

## 参与贡献

欢迎贡献代码，PR请提交到`develop`分支


## 界面预览

![文档管理](./front/public/static/images/table.png "table.png")

![编辑接口](./front/public/static/images/edit.png "edit.png")

![浏览文档](./front/public/static/images/view.png "view.png")

![调试接口](./front/public/static/images/debug.png "debug.png")


## 沟通交流

<img src="./front/public/static/images/group.jpg" width="50%" height="50%" />
