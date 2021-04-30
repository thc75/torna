# 更新日志

## 1.6.0

- [feat]支持Query参数
- [feat]支持文档复制
- [feat]推送接口可指定推送人
- [enhance]优化接口调试，可记住调试参数
- [enhance]优化文档导出，可勾选部分文档
- [enhance]公共请求参数支持数据节点
- [enhance]公共返回参数支持添加子节点
- [fix]修复文档分享勾选文档问题

## 1.5.0

- [feat]支持Dubbo文档展示
- [fix]修改文档出现新增文档问题 [#I3O0E6](https://gitee.com/durcframework/torna/issues/I3O0E6)
- [fix]推送不显示错误码问题
- [enhance]接口调试页自动格式化json

## 1.4.3

- [enhance]优化文档编辑页预览
- [fix]修复公共返回参数显示

## 1.4.2

- [enhance]优化文档返回示例显示
- [enhance]文档页新增请求示例
- [enhance]文档页菜单显示HttpMethod
- [fix]修复删除参数问题 [#I3NCRT](https://gitee.com/durcframework/torna/issues/I3NCRT)

## 1.4.1

- [enhance]公共请求参数支持定义子节点 [#I3M33I](https://gitee.com/durcframework/torna/issues/I3M33I)
- [enhance]docker启动指定时区 [#I3MDPH](https://gitee.com/durcframework/torna/issues/I3MDPH)

## 1.4.0

- [feat]新增文档分享功能

## 1.3.4

- [fix]调试接口上传文件、下载文件问题

## 1.3.3

- [enhance]优化文档浏览页描述字段显示，支持HTML标签
- [enhance]支持多级推送（嵌套文件夹）
- [fix]修复接口更新返回值对象后推送不更新问题 [#I3IGWT](https://gitee.com/durcframework/torna/issues/I3IGWT)

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