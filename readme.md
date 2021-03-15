# Torna

企业接口文档解决方案，目标是让文档管理变得更加方便、快捷。Torna采用团队协作的方式管理和维护项目API文档，将不同形式的文档纳入进来，形成一个统一的维护方式。

<img src="./front/public/static/images/arc.png" width="80%" height="80%" />

## 特性介绍

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持导出为markdown格式、html格式
- 支持多环境接口调试
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰
- 部署简单，直接运行脚本启动程序
- 支持docker运行

---

[演示地址](http://demo.torna.cn/) 演示账号：`guest@torna.cn` 密码：`123456`

当前账号是访问者，只能阅读文档，可以自行部署到本地体验更多功能。

## 使用步骤

- 准备工作

    - Java环境，最低要求Java8
    - MySQL，要求5.6.5及以后，5.6.5之前的版本见：[支持低版本MySQL](http://torna.cn/dev/mysql-lower-version.html)

前往 [发行版页面](https://gitee.com/durcframework/torna/releases) ，下载最新版本，解压zip

导入数据库，执行[mysql.sql](./mysql.sql)

打开`application.properties`配置文件，修改数据库连接配置

执行`sh startup.sh`启动（Windows执行`startup.bat`）

访问：`http://ip:7700`

### docker运行

下载公共镜像

`docker pull tanghc2020/torna:latest`

导入数据库，执行[mysql.sql](./mysql.sql)

复制`server/boot/src/main/resources/application.properties`文件到`/opt/torna/config`下，修改数据库连接配置

执行`docker run --name torna -p 7700:7700 -v /opt/torna/config:/torna/config -d <镜像ID>`

浏览器访问`http://ip:7700/`


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
## 开发部署

参见：[开发文档](http://torna.cn/dev/)

## 更新日志

[changelog](./changelog.md)

## 界面预览

![文档管理](./front/public/static/images/table.png "table.png")

![编辑接口](./front/public/static/images/edit.png "edit.png")

![浏览文档](./front/public/static/images/view.png "view.png")

![调试接口](./front/public/static/images/debug.png "debug.png")


## 沟通交流

<img src="./front/public/static/images/group.jpg" width="50%" height="50%" />
