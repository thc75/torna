# Torna

一体化的企业级接口文档解决方案，目标是让文档管理变得更加方便、快捷。

## 特性

- 支持接口文档CRUD
- 支持接口请求
- 支持查看文档变更历史，内容对比
- 支持字典管理
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 支持导入外部文档（已支持导入swagger，未来会支持导入postman）
- 支持OpenAPI管理文档
- 支持导出为markdown格式、html格式
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰

## 开发部署

- 运行环境：Java8, Mysql5.7+
- 开发环境：Java8, Maven3, Nodejs11+, Mysql5.7+


1. 导入Mysql脚本
2. 打开`start/src/main/resources/application-dev.properties`，修改数据库配置
3. 运行`start/src/main/java/torna/TornaApplication.java`
4. 运行前端，见：`front/README.md`


体验账号：

```
密码均为：123456

超级管理员：admin@torna.org

研发一部空间管理员：dev1admin@torna.org
研发一部-商城项目（公开）-项目管理员：dev1shop_admin@torna.org
研发一部-商城项目（公开）-开发者张三：dev1shop_zhangsan@torna.org
研发一部-访客王五：dev1guest_wangwu@torna.org


研发二部空间管理员：dev2admin@torna.org
研发二部-后台项目（私有）-项目管理员：dev2back_admin@torna.org
研发二部-后台项目（私有）-开发者李四：dev2back_lisi@torna.org
研发二部-后台项目（私有）-访客：dev2back_guest@torna.org
研发二部-访客赵六：dev2guest_zhaoliu@torna.org
```



