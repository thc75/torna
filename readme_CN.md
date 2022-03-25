# Torna

[English](readme.md) | 简体中文

企业接口文档解决方案，目标是让文档管理变得更加方便、快捷。Torna采用团队协作的方式管理和维护项目API文档，将不同形式的文档纳入进来，形成一个统一的维护方式。

Torna弥补了传统文档生成工具（如swagger）的不如之处，在保持原有功能的前提下丰富并增强了一些实用的功能。

<img src="./front/public/static/images/arc2.png" width="80%" height="80%" />

## 当前版本号

| 模块 |                                       版本                                        |
| :----: |:-------------------------------------------------------------------------------:|
| Torna |                                     1.14.4                                      |
| sdk-java |       ![maven](https://img.shields.io/maven-central/v/cn.torna/torna-sdk)       |
| swagger-plugin |    ![maven](https://img.shields.io/maven-central/v/cn.torna/swagger-plugin)     |
| smart-doc | ![maven](https://img.shields.io/maven-central/v/com.github.shalousun/smart-doc) |


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

- 登录账号：

用户名：`admin`，密码：`123456`

- 后续升级

无特殊说明，只需要覆盖`torna.jar文件`和`dist文件夹`，然后重启即可


### 方式2：docker运行

导入数据库，执行[mysql.sql](./mysql.sql)

下载公共镜像

`docker pull tanghc2020/torna:latest`

下载完成后，执行docker命令：

```
docker run --name torna --restart=always \
  -p 7700:7700 \
  -e JAVA_OPTS="-Xms256m -Xmx256m" \
  -e MYSQL_HOST="172.16.60.102:3306" \
  -e MYSQL_SCHEMA="torna" \
  -e MYSQL_USERNAME="root" \
  -e MYSQL_PASSWORD="root" \
  -d tanghc2020/torna:latest
```

需改更改的部分：

- MYSQL_HOST：MySQL服务器地址
- MYSQL_SCHEMA：数据库名称，默认不用改
- MYSQL_USERNAME：MySQL用户名，要求账号能运行DDL/DML/ALTER语句
- MYSQL_PASSWORD：MySQL密码

浏览器访问`http://ip:7700`，ip对应docker宿主机器ip，非docker容器ip


### docker-compose部署torna
[【docker-compose方式部署torna】](https://gitee.com/durcframework/torna/tree/master/torna-docker-compose)

### kubernetes部署torna
[【kubernetes部署torna】](https://gitee.com/durcframework/torna/tree/master/torna-on-kubernetes)

## 推荐组合

**smart-doc + Torna实现文档全流程自动化**

如果您使用Java语言，推荐使用`smart-doc + Torna`

[smart-doc](https://gitee.com/smart-doc-team/smart-doc) + Torna 组成行业领先的文档生成和管理解决方案，使用smart-doc无侵入完成Java源代码和注释提取生成API文档，自动将文档推送到Torna企业级接口文档管理平台。

通过这套组合您可以实现：只需要写完Java注释就能把接口信息推送到Torna平台，从而实现接口预览、接口调试。

推送的内容有：`接口名称/author/Path参数/Header/请求参数/返回参数/字典列表/公共错误码`

如果您是非Java语言，可以使用表单页面编辑以上内容，完成后同样可以进行接口预览、调试。

## 开发部署

参见：[开发文档](http://torna.cn/dev/)

## 其它资源

- [Torna对应的示例工程](https://gitee.com/durcframework/torna-example) ，演示swagger插件的用法

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
