# ISV管理后台

  前提：先安装好npm，[npm安装教程](https://blog.csdn.net/zhangwenwu2/article/details/52778521)

- 启动服务端程序
- 执行`npm install --registry=https://registry.npm.taobao.org`
- 执行`npm run dev`，访问`http://localhost:9530/`


## 修改端口号

打开`vue.config.js`，找到`port`属性

## 修改请求url

- 开发环境：打开`.env.development`文件，修改`VUE_APP_BASE_API`
- 生产环境：打开`.env.production`文件，修改`VUE_APP_BASE_API`

## 构建

开发完毕后，运行`build.sh`脚本，生成后的内容在dist目录