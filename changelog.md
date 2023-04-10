# 更新日志

## 1.21.0

- 【新增】
    - 支持覆盖更新，可更新局部文档
    - 后台管理支持给用户批量分配项目

- 【优化】
    - mock调试优化

- 【修复】
    - 所属分类 bug [#I6QSF9](https://gitee.com/durcframework/torna/issues/I6QSF9)
    - swagger应用同步问题 [#I6RVL0](https://gitee.com/durcframework/torna/issues/I6RVL0)


## 1.20.3

- 【修复】多个调试环境公共header切换错乱问题

## 1.20.2

- 【修复】swagger推送不显示maxLength问题


## 1.20.1

- 【修复】推送文档dataId太长导致保存数据错误

## 1.20.0

- 【新增】可自定义扩展i18n（后台管理-系统设置-国际化配置）
- 【优化】swagger导入支持非ref的嵌套对象
- 【优化】优化应用配置UI
- 【优化】调整前端目录结构

## 1.19.4

- 【修复】分享页面无法显示文档信息

## 1.19.3

- 【修复】分享页面无法打开dubbo文档问题 [#I6768Y](https://gitee.com/durcframework/torna/issues/I6768Y)
- 【优化】LDAP登录在自定义filter的情况不使用spring封装的template登录 [#I67767](https://gitee.com/durcframework/torna/issues/I67767)

## 1.19.2

- 【修复】dubbo文档无法显示问题

## 1.19.1

- 【修复】分享页面跳转到登录页问题

## 1.19.0

- 【新增】富文本编辑器支持上传图片，支持上传到阿里云oss、七牛云kodo、s3
- 【新增】支持纯富文本编辑创建文档

## 1.18.2

- 【优化】优化swagger导入，支持数组body（`[1,2,3]`）

## 1.18.1

- 【优化】支持接口管理目录移动 [#I60RQ5](https://gitee.com/durcframework/torna/issues/I60RQ5)
- 【优化】支持项目移动至不同的空间 [#I60IET](https://gitee.com/durcframework/torna/issues/I60IET)
- 【修复】设置了contextPath后图片无法加载问题 [#I60QLT](https://gitee.com/durcframework/torna/issues/I60QLT)
- 【修复】当参数选择了根数据后，没有办法选择关联字典 [#I5ZR0O](https://gitee.com/durcframework/torna/issues/I5ZR0O)
- 【修复】dubbo接口太长无法保存问题 [#I5Y8GO](https://gitee.com/durcframework/torna/issues/I5Y8GO)
- 【修复】多级文档目录无法分享问题

## 1.18.0

- 【新增】新增常量管理，可管理项目中的错误码、枚举信息
- 【新增】导出文档的时候希望能够选择相应的环境 [#I5SOET](https://gitee.com/durcframework/torna/issues/I5SOET);
- 【修复】swagger query对象参数不显示；导入swagger树状类无限递归问题  [pr](https://gitee.com/durcframework/torna/pulls/42)
- 【优化】优化LDAP登录
- 【优化】优化部分UI
- 【优化】手动修改文档描述后，再次推送不受影响，以手动调整为准
- 【调整】之前的`模块`称呼调整为`应用`

## 1.17.3

- 【修复】修复LDAP登录界面不显示问题

## 1.17.2

- 【新增】新增接口可返回mock脚本
- 【新增】推送接口新增`isOverride`参数，可推送修改部分接口
- 【修复】修复导入swagger文档报错问题

## 1.17.1

- 【修复】字典删除无法再次添加问题 [#I5SB89](https://gitee.com/durcframework/torna/issues/I5SB89)
- 【修复】升级jar版本，修复安全漏洞 [#I5T656](https://gitee.com/durcframework/torna/issues/I5T656)
- 【修复】分享链接里面的接口没有进行排序 [#I5TF2N](https://gitee.com/durcframework/torna/issues/I5TF2N)
- 【优化】优化docker构建，更好的适配云端构建

## 1.17.0

- 【修复】Postman导入异常 [#I5R939](https://gitee.com/durcframework/torna/issues/I5R939)
- 【修复】分享页面修改配置无法显示调试环境问题
- 【升级】springboot升级到2.6.11，支持Java17 [#I5R2RA](https://gitee.com/durcframework/torna/issues/I5R2RA)
- 【升级】spring-ldap升级到2.4.1（解决Java17启动`cannot access class com.sun.jndi.ldap.LdapCtxFactory`问题）
- 【新增】可自定义文档排序规则（后台管理-系统设置-文档排序设置）
- 【优化】分享文档可复制URL

## 1.16.3

- 【修复】请求头参数异常 [#I5Q5HD](https://gitee.com/durcframework/torna/issues/I5Q5HD)
- 【新增】文档预览页新增字典查看按钮
- 【新增】项目列表新增列表展示

## 1.16.2

- 【优化】导入Postman支持单值数组参数（`[123,456]`）
- 【修复】切换调试环境导致临时请求参数增多问题

## 1.16.1

- 【修复】文档导出多层级无数据的问题 [pr](https://gitee.com/durcframework/torna/pulls/37)
- 【修复】推送枚举展示错误问题 [#I5L310](https://gitee.com/durcframework/torna/issues/I5L310)
- 【修复】postman导入数据报错 [#I5KZ4Z](https://gitee.com/durcframework/torna/issues/I5KZ4Z)
- 【优化】优化文档浏览页tab样式

## 1.16.0

- 【优化】优化swagger文档导入，提供三种导入方式（插件导入，URL导入，json/yaml导入）
- 【修复】单值返回参数显示问题 [#I5JGPH](https://gitee.com/durcframework/torna/issues/I5JGPH)

## 1.15.8

- 【修复】文档预览页tab切换出现空白页面问题
- 【优化】调试页json编辑器高度
- 【优化】dubbo页面，显示接口名称、作者

## 1.15.7

- 【优化】文档浏览tab页展示
- 【优化】调试页面新增json编辑器

## 1.15.6

- 【修复】dubbo文档不显示问题

## 1.15.5

- 【优化】公共请求头、公共请求参数可以指定是否必填

## 1.15.4

- 【修复】指定context-path分享链接错误 [#I5EA4H](https://gitee.com/durcframework/torna/issues/I5EA4H)

## 1.15.3

- 【修复】markdown内容无法编辑问题

## 1.15.2

- 【修复】超级管理员无法查看空间下的项目

## 1.15.1

- 【新增】文档可导出为word [pr](https://gitee.com/durcframework/torna/pulls/34)
- 【升级】fastmybatis升级到2.3.2
- 【优化】优化用户查询SQL
- 【优化】增大axios超时时间，方便调试
- 【优化】添加部分权限校验
- 【优化】富文本编辑器使用wangeditor

## 1.15.0

- 【新增】新增分享配置时可选调试环境 [pr](https://gitee.com/durcframework/torna/pulls/28)
- 【新增】接口文档编辑和查看描述以及备注的地方使用富文本编辑器 [pr](https://gitee.com/durcframework/torna/pulls/32)
- 【新增】调试页面新增自定义修改请求类型或者参数 [#I545M6](https://gitee.com/durcframework/torna/issues/I545M6)
- 【优化】推送失败打印具体文档信息
- 【优化】代理调试新增对https的支持 [pr](https://gitee.com/durcframework/torna/pulls/30)
- 【修复】postman上传get请求类型失败 [pr](https://gitee.com/durcframework/torna/pulls/31)
- 【修复】编辑文档响应参数的时候敲回车会报错 [#I57TQ3](https://gitee.com/durcframework/torna/issues/I57TQ3)
- 【修复】用户为空间成员，非项目成员；浏览模式下访问公开项目显示以上异常 [#I57UMS](https://gitee.com/durcframework/torna/issues/I57UMS)

## 1.14.5

- 【修复】修复导出和query参数展示bug

## 1.14.4

- 【修复】修复分享整个模块只显示部分文档问题
- 【修复】修复调试返回非200状态不显示错误信息问题 [#I4Y25X](https://gitee.com/durcframework/torna/issues/I4Y25X)

## 1.14.3

- 【优化】优化消息推送 [#I4XGER](https://gitee.com/durcframework/torna/issues/I4XGER)
- 【升级】升级fastmybatis到2.1.0

## 1.14.2

- 【新增】文档浏览页新增tab标签。默认未启用，启用方式：个人中心-系统设置
- 【优化】优化LDAP登录

## 1.14.1

- 【修复】推送报空指针异常问题

## 1.14.0

- 【新增】推送文档新增`deprecated`指定废弃信息
- 【优化】文档浏览页可复制`请求示例`和`返回示例` 感谢`liuxm提供pr`
- 【优化】登录页可选择语言 感谢`liuxm提供pr`
- 【优化】推送完毕打印日志
- 【优化】优化文档浏览页调整页面大小左边菜单显示问题 [#I4U3ZT](https://gitee.com/durcframework/torna/issues/I4U3ZT)
- 【移除】swagger url导入功能改用插件推送
- 【升级】fastmybatis升级到2.0.0

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