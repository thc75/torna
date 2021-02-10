# Torna

企业接口文档解决方案，目标是让文档管理变得更加方便、快捷。

官网：http://torna.cn

## 特性

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持查看文档变更历史，内容对比
- 支持导出为markdown格式、html格式
- 支持多环境接口调试
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰
- 部署简单，直接运行打包脚本即可完成构建

## 工程说明

- front: 前端工程
- script: 辅助脚本
- sdk: OpenAPI对应的SDK
- server: 服务端工程
- mysql.sql：数据库脚本

## 开发部署

```
开发环境：Java8, Maven3, Nodejs12+, Mysql5.7+
软件架构：服务端 SpringBoot-2.3.4.RELEASE，前端 elementUI-2.13.0 + vue2
```

- 导入Mysql脚本
- IDE安装lombok插件，然后打开项目(IDEA下可以打开根pom.xml，然后open as project)
- 打开`server/boot/src/main/resources/application.properties`，修改数据库配置
- 运行`server/boot/src/main/java/cn/torna/TornaApplication.java`
- 运行前端，见：[front/README.md](./front/README.md)


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

## 打包发布

> 运行环境：Java8, Mysql5.7+

- `Linux/Mac`：执行`build.sh`
- `Windows系统`：执行`build.bat`

构建结果在`dist/torna`目录，最终结构如下：

```
torna                           # 根目录
├── application.properties      # 配置文件
├── dist                        # 前端资源
├── debug.sh                    # 线上调试
├── shutdown.sh                 # 结束服务脚本
├── startup.sh                  # 启动服务脚本
└── torna.jar                   # 服务程序
```

把`torna`文件夹上传到服务器

修改`application.properties`配置文件内容，改为线上配置

执行`startup.sh`，启动应用

访问：`http://ip:7700`

---

讨论Q群：194673097
