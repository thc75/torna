# 图片上传配置

使用`超级管理员`账号登录，前往 `后台管理 - 系统设置`

<img src="./static/help/images/upload.png" style="height: 300px" />

## 上传文件存放目录

图片上传默认保存到`${user_home}/torna_upload`文件夹下，可自定义其它文件夹，末尾没有`/`，如：`/opt/upload`

## 自定义域名

默认情况下使用Torna服务域名访问图片，如果想要自定义域名访问图片，操作步骤如下：

将`/opt/upload`目录映射成为静态资源服务，比如使用nginx代理静态资源

要求访问：`http://images.xxx.com` 就是访问 `/opt/upload`目录

比如:`http://images.xxx.com/2022/12/6/xxxx.png` 对应 `/opt/upload/2022/12/6/xxxx.png`

