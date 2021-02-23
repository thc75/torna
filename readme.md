# Torna

企业接口文档解决方案，目标是让文档管理变得更加方便、快捷。

[官网](http://torna.cn)

## 特性介绍

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持查看文档变更历史，内容对比
- 支持导出为markdown格式、html格式
- 支持多环境接口调试
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰
- 部署简单，直接运行脚本启动程序

## 使用步骤

- 准备工作

    - Java环境，最低要求Java8
    - MySQL，要求5.6.5及以后，5.6.5之前的版本见：[支持低版本MySQL](http://torna.cn/dev/mysql-lower-version.html)

执行[mysql.sql](./mysql.sql)，导入数据库

前往发行版页面，下载最新版本，解压zip

打开`application.properties`配置文件，修改数据库链接配置

执行`sh startup.sh`启动（Windows执行`startup.bat`）

访问：`http://ip:7700`

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

## 开发部署

参见：[开发文档](http://torna.cn/dev/)


## 界面预览

![文档管理](https://images.gitee.com/uploads/images/2021/0210/095322_c94cd7bf_332975.png "table.png")

![编辑接口](https://images.gitee.com/uploads/images/2021/0210/095338_01fd24b6_332975.png "edit.png")

![浏览文档](https://images.gitee.com/uploads/images/2021/0210/095347_734567f3_332975.png "view.png")

![调试接口](https://images.gitee.com/uploads/images/2021/0210/095359_4d9a5182_332975.png "debug.png")


## 沟通交流

Q群：194673097
