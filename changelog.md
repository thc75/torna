# 更新日志

## 1.8.4

- 【修复】修复调试页提交数组参数问题
- 【优化】文档预览页面可调整菜单显示宽度（resizeBar）
- 【新增】推送接口新增`isReplace`参数，决定文档是否替换

## 1.8.3

- 【优化】后台添加的用户可重置密码
- 【修复】聚合空间添加项目时，无内容模块会被添加为空 [#I3UG1H](https://gitee.com/durcframework/torna/issues/I3UG1H)

## 1.8.2

- 【优化】优化文件上传
- 【优化】优化swagger插件
- 【修复】修复导入json参数类型不对问题

## 1.8.1

- 【增强】支持定义数组body/数组返回
- 【增强】接口描述支持html标签
- 【增强】调试环境新增是否公开属性
- 【增强】调试页参数可勾选（CheckBox）
- 【优化】优化接口描述字段显示 [pr](https://gitee.com/durcframework/torna/pulls/9)

## 1.8.0

- 【特性】新增聚合空间，可聚合其它空间中的文档统一展示
- 【增强】新增swagger插件，自动将本地文档推送到Torna服务器
- 【增强】推送接口支持排序字段

## 1.7.3

- 【增强】支持Nginx配置contextPath
- 【增强】支持批量推送枚举

## 1.7.2

- 【修复】swagger导入问题
- 【修复】钉钉登录下项目成员查询问题
- 【增强】优化调试页面请求数组问题

## 1.7.1

- 【增强】分享页可切换语言

## 1.7.0

- 【特性】支持钉钉免密登录 [doc](http://torna.cn/dev/third-party-login.html)
- 【特性】界面支持中英文切换（个人中心-系统设置）
- 【增强】优化导出功能
- 【更新】ElementUI升级到2.15.1

# 1.6.3

- 【增强】优化mock，可指定path
- 【增强】优化推送

## 1.6.2

- 【增强】文档新增`维护人`字段

## 1.6.1

- 【修复】修复无法重置密码问题

## 1.6.0

- 【特性】支持Query参数
- 【特性】支持文档复制
- 【特性】推送接口可指定推送人
- 【增强】优化接口调试，可记住调试参数
- 【增强】优化文档导出，可勾选部分文档 [#I3O0CB](https://gitee.com/durcframework/torna/issues/I3O0CB)
- 【增强】公共请求参数支持数据节点
- 【增强】公共返回参数支持添加子节点
- 【修复】修复文档分享勾选文档问题

## 1.5.0

- 【特性】支持Dubbo文档展示
- 【修复】修改文档出现新增文档问题 [#I3O0E6](https://gitee.com/durcframework/torna/issues/I3O0E6)
- 【修复】推送不显示错误码问题
- 【增强】接口调试页自动格式化json

## 1.4.3

- 【增强】优化文档编辑页预览
- 【修复】修复公共返回参数显示

## 1.4.2

- 【增强】优化文档返回示例显示
- 【增强】文档页新增请求示例
- 【增强】文档页菜单显示HttpMethod
- 【修复】修复删除参数问题 [#I3NCRT](https://gitee.com/durcframework/torna/issues/I3NCRT)

## 1.4.1

- 【增强】公共请求参数支持定义子节点 [#I3M33I](https://gitee.com/durcframework/torna/issues/I3M33I)
- 【增强】docker启动指定时区 [#I3MDPH](https://gitee.com/durcframework/torna/issues/I3MDPH)

## 1.4.0

- 【特性】新增文档分享功能

## 1.3.4

- 【修复】调试接口上传文件、下载文件问题

## 1.3.3

- 【增强】优化文档浏览页描述字段显示，支持HTML标签
- 【增强】支持多级推送（嵌套文件夹）
- 【修复】修复接口更新返回值对象后推送不更新问题 [#I3IGWT](https://gitee.com/durcframework/torna/issues/I3IGWT)

## 1.3.2

- 【修复】参数删除后推送文档，再还原后推送不显示问题

## 1.3.1

- 【特性】推送支持path参数
- 【修复】多线程并发情况下推送文档丢失问题

## 1.3.0

- 支持第三方登录（OAuth） [doc](http://torna.cn/dev/third-party-login.html)

## 1.2.4

- 【增强】优化树形表格显示 [#I3EHAP](https://gitee.com/durcframework/torna/issues/I3EHAP)
- 【修复】同步swagger参数没有更新问题
- 【修复】保存文档基本信息无效问题

## 1.2.3

- 【特性】调试接口可选择是否使用代理

## 1.2.2

- 【修复】Java11无法打包问题

## 1.2.1

- 【修复】推送获取错误token问题

## 1.2.0

- 【特性】新增全局请求参数
- 【特性】新增全局返回参数
- 【优化】优化字典管理交互
- 【优化】优化模块设置交互
- 【优化】优化加载文档性能问题


## 1.1.3

- 优化smart-doc推送url显示

## 1.1.2

- 【修复】smart-doc推送无法删除旧文档问题 [#I3CPJL](https://gitee.com/durcframework/torna/issues/I3CPJL)

## 1.1.1

- 【修复】smart-doc推送无法显示文档问题 [#I3CSJS](https://gitee.com/durcframework/torna/issues/I3CSJS)

## 1.1.0

- 【特性】支持Mock
- 【修复】文档分类无法删除问题 [#I3CPJ5](https://gitee.com/durcframework/torna/issues/I3CPJ5)
- 【修复】smart-doc推送无法删除旧文档问题 [#I3CPJL](https://gitee.com/durcframework/torna/issues/I3CPJL)

## 1.0.2

- 【修复】同步swagger前端报错问题
- 【修复】Postman导入后无法调试问题
- 【特性】升级服务

## 1.0.1

- 【特性】支持docker部署 [#I3AWCB](https://gitee.com/durcframework/torna/issues/I3AWCB)
- 【修复】OpenAPI推送无法删除字段
- 【修复】进入admin管理页报错问题
- 【修复】调试页面跳转模块管理无法设置调试环境问题

## 1.0.0

- 支持接口文档增删改查
- 支持导入外部接口（支持导入swagger、postman）
- 支持OpenAPI管理接口
- 支持字典管理
- 支持导出为markdown格式、html格式
- 支持多环境接口调试
- 支持文档权限管理，访客、开发者、管理员对应不同权限
- 提供`管理模式`和`浏览模式`双模式，管理模式用来编辑文档内容，浏览模式纯粹查阅文档，界面无其它元素干扰