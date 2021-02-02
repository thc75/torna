# 服务端

## 模块说明

- boot：负责启动&文件配置
- server-api：OpenAPI相关接口，基于 [easyopen](https://gitee.com/durcframework/easyopen) 实现
- server-common：公共的类，util等文件
- server-dao：数据库操作层，存放Mapper和实体类，基于 [fastmybatis](https://gitee.com/durcframework/fastmybatis)
- server-manager：第三方服务类
- server-service：业务层，处理具体业务
- server-web：控制层，存放Controller