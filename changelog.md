# 更新日志

## 1.3.2

- [fix]参数删除后推送文档，再还原后推送不显示问题

## 1.3.1

- [feat]推送支持path参数
- [fix]多线程并发情况下推送文档丢失问题

## 1.3.0

- 支持第三方登录（OAuth） [doc](http://torna.cn/dev/third-party-login.html)

## 1.2.4

- [enhance]优化树形表格显示 [#I3EHAP](https://gitee.com/durcframework/torna/issues/I3EHAP)
- [fix]同步swagger参数没有更新问题
- [fix]保存文档基本信息无效问题

## 1.2.3

- [feat]调试接口可选择是否使用代理

## 1.2.2

- [fix]Java11无法打包问题

## 1.2.1

- [fix]推送获取错误token问题

## 1.2.0

- [feat]新增全局请求参数
- [feat]新增全局返回参数
- [refactor]优化字典管理交互
- [refactor]优化模块设置交互
- [refactor]优化加载文档性能问题


## 1.1.3

- 优化smart-doc推送url显示

## 1.1.2

- [fix]smart-doc推送无法删除旧文档问题 [#I3CPJL](https://gitee.com/durcframework/torna/issues/I3CPJL)

## 1.1.1

- [fix]smart-doc推送无法显示文档问题 [#I3CSJS](https://gitee.com/durcframework/torna/issues/I3CSJS)

## 1.1.0

- [feat]支持Mock
- [fix]文档分类无法删除问题 [#I3CPJ5](https://gitee.com/durcframework/torna/issues/I3CPJ5)
- [fix]smart-doc推送无法删除旧文档问题 [#I3CPJL](https://gitee.com/durcframework/torna/issues/I3CPJL)

## 1.0.2

- [fix]同步swagger前端报错问题
- [fix]Postman导入后无法调试问题
- [feat]升级服务

## 1.0.1

- [feat]支持docker部署 [#I3AWCB](https://gitee.com/durcframework/torna/issues/I3AWCB)
- [fix]OpenAPI推送无法删除字段
- [fix]进入admin管理页报错问题
- [fix]调试页面跳转模块管理无法设置调试环境问题

## 1.0.0

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持导出为markdown格式、html格式
- 支持多环境接口调试
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰