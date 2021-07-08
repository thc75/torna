# Torna前端页面

> 前提：先安装好nodejs，建议安装nodejs12+，版本太高可能会有问题

- 执行`npm install --registry=https://registry.npm.taobao.org`
- 执行`npm run dev`，访问`http://localhost:9530/`


## 修改端口号

打开`vue.config.js`，找到`port`属性


## 构建

执行`npm run build:prod`，或者运行`build.sh`脚本，构建结果在dist目录