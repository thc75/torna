# 更新日志

## 1.14.0

- 【新增】推送文档新增`deprecated`指定废弃信息
- 【优化】文档浏览页可复制`请求示例`和`返回示例` 感谢`liuxm提供pr`
- 【优化】登录页可选择语言 感谢`liuxm提供pr`
- 【优化】推送完毕打印日志
- 【优化】优化文档浏览页调整页面大小左边菜单显示问题 [#I4U3ZT](https://gitee.com/durcframework/torna/issues/I4U3ZT)
- 【移除】swagger url导入功能改用插件推送

## 1.13.1

- 【优化】调整HttpMethod组件Tag样式
- 【优化】文档预览页可复制接口URL

## 1.13.0

- 【新增】分享管理新增能否调试接口功能
- 【新增】浏览页新增【空间维度】和【项目维度】
- 【新增】文档预览页可复制字段
- 【修复】修复调试页跳转到登录页问题 [#I4SAJP](https://gitee.com/durcframework/torna/issues/I4SAJP)
- 【修复】修复调试页面上传文件表单显示错误

## 1.12.2

- 【修复】修复调试环境数据迁移BUG
- 【修复】从其他模块导入公共参数，子节点没有导过来 [#I4QS67](https://gitee.com/durcframework/torna/issues/I4QS67)
- 【修复】使用枚举传参，调试选项中无选择项 [#I4QZB2](https://gitee.com/durcframework/torna/issues/I4QZB2)
- 【修复】修复删除公共参数没有删除子节点问题
- 【修复】修复文档编辑页无法更改枚举字段问题
- 【优化】优化枚举字段显示 

## 1.12.1

- 【修复】修复调试无法保存代理勾选状态
- 【优化】去除非必须依赖减小jar包大小

## 1.12.0

- 【优化】调试环境改造，数据迁移
- 【优化】调试显示图片
- 【优化】文档预览页树菜单显示接口数量
- 【优化】优化参数描述内容过长显示
- 【优化】优化国际化显示
- 【优化】优化LDAP登录，登录后同步邮箱
- 【修复】修复接口调试\n问题 [#I4KODO](https://gitee.com/durcframework/torna/issues/I4KODO)
- 【升级】fastmybatis升级到1.10.11
- 【新增】文档表格页可以新增分类

## 1.11.3

- 【修复】修复代理转发content-type错误
- 【优化】优化调试接口文件导出功能

## 1.11.2

- 【修复】非代理模式下请求多余header [#I4INZZ](https://gitee.com/durcframework/torna/issues/I4INZZ)
- 【修复】聚合接口分享输入密码跳转登录问题

## 1.11.1

- 【修复】修复枚举信息不显示问题 [#I4I7DZ](https://gitee.com/durcframework/torna/issues/I4I7DZ)
- 【修复】修复保存mock脚本js错误

## 1.11.0

- 【新增】支持LDAP登录 [doc](http://torna.cn/dev/third-party-login.html#ldap%E7%99%BB%E5%BD%95)
- 【优化】优化第三方接入返回结果嵌套对象问题
- 【优化】优化登录、注册页面国际化显示
- 【优化】简化文档推送

## 1.10.6

- 【修复】DELETE请求无法传递body问题 [#I4GAAU](https://gitee.com/durcframework/torna/issues/I4GAAU)

## 1.10.5

- 【优化】优化字典展示
- 【优化】请求参数可以选择字典项

## 1.10.4

- 【修复】修复推送删除了自定义错误码问题

## 1.10.3

- 【修复】修复聚合浏览页面点击调试需要登录问题

## 1.10.2

- 【修复】修复下载二进制文件错误
- 【修复】修复无法格式化json问题

## 1.10.1

- 【修复】修复聚合项目预览页登录失败BUG
- 【优化】优化swagger插件 [PR](https://gitee.com/durcframework/torna/pulls/15)
- 【优化】优化聚合项目预览页左侧树展示
- 【优化】优化聚合项目公共参数展示
- 【优化】优化文档预览页展示

## 1.10.0

- 【修复】模块无法删除问题 [I3I2B1](https://gitee.com/durcframework/torna/issues/I3I2B1) [I3ZG1Q](https://gitee.com/durcframework/torna/issues/I3ZG1Q)
- 【修复】请求支持PATCH方法 [I46MAP](https://gitee.com/durcframework/torna/issues/I46MAP)
- 【优化】用户管理的优化 [I424OB](https://gitee.com/durcframework/torna/issues/I424OB )
- 【优化】有重复header优先使用公共的 [I3ZO8R](https://gitee.com/durcframework/torna/issues/I3ZO8R)
- 【优化】不记住公共请求头 
- 【优化】根据环境保存Debug配置
- 【新增】增加接口更新锁定 [I465C7](https://gitee.com/durcframework/torna/issues/I465C7)
- 【新增】页左侧树自动打开

## 1.9.4

- 【修复】修复mock，移除请求参数部分

## 1.9.3

- 【优化】优化Postman导入
- 【优化】成员选择控件
- 【增强】增强聚合文档展示，支持自定义页面

## 1.9.2

- 【修复】简单数组类型显示方式错误 [#I3ZFCU](https://gitee.com/durcframework/torna/issues/I3ZFCU)
- 【优化】优化markdown导出 [#I3Z9NS](https://gitee.com/durcframework/torna/issues/I3Z9NS)

## 1.9.1

- 【增强】mock添加一个配置，忽略query + body
- 【优化】mock的content-type默认选择json
- 【优化】优化Postman导入
- 【修复】修复推送出现NPE问题

## 1.9.0

- 【新增】排序字段，可调整文档/参数顺序
- 【增强】文档表格新增收缩/展开控制
- 【修复】修复swagger插件泛型参数展示问题
- 【修复】修改文档空指针问题
- 【修复】导入postman空指针问题

## 1.8.7

- 【优化】优化文档搜索

## 1.8.6

- 【修复】mock路径后面加queryString无法访问

## 1.8.5

- 【优化】优化登录失败报错日志
- 【增强】admin后台新增用户id查询条件

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