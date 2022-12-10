# 图片上传配置

图片上传分为两种方式：上传到本地服务器、上传到云服务器（阿里云oss、七牛云kodo、S3）

## 1. 上传到本地服务器

将图片保存到本地服务器，然后由Torna代理返回图片资源

### 1.1 docker部署

如果使用docker部署Torna，需要挂载一个宿主机目录，用来存放上传的文件，假设宿主机目录是：`/opt/upload`

图片上传默认保存到`${user_home}/torna_upload`文件夹下，容器默认的用户是root，因此是：`/root/torna_upload`

启动脚本新增一句：`-v /opt/upload:/root/torna_upload \`

将容器内部`/root/torna_upload`目录映射到容器外部`/opt/upload`

文件上传实际将会存放在`/opt/upload`下

重启Torna镜像

### 1.2 非docker部署

使用`超级管理员`账号登录，前往 `后台管理 - 系统设置`

<img src="./static/help/images/upload.png" style="height: 300px" />

- 上传文件存放目录

文本框输入文件夹绝对路径，末尾没有`/`，如：`/opt/upload`

修改后立即生效，无需重启Torna

### 1.3 自定义域名

默认情况下使用Torna服务域名访问图片，如果想要自定义域名访问图片，操作步骤如下：

将`/opt/upload`目录映射成为静态资源服务，比如使用nginx代理静态资源

要求访问：`http://images.xxx.com` 就是访问 `/opt/upload`目录

比如:`http://images.xxx.com/2022/12/6/xxxx.png` 对应 `/opt/upload/2022/12/6/xxxx.png`

修改后立即生效，无需重启Torna

## 2. 上传到云服务器

Torna支持将图片上传到云服务器，目前支持`阿里云OSS`、`七牛云kodo`、`S3`

### 2.1 使用阿里云OSS存储

`application.properties`配置文件新增如下配置：

```
# 地域节点
aliyun.oss.endpoint=
# AccessKey ID
aliyun.oss.access-key=
# AccessKey Secret
aliyun.oss.access-secret=
# 存储空间，需要提前建好
aliyun.oss.bucket-name=
```

重启Torna

### 2.2 使用七牛云kodo存储

`application.properties`配置文件新增如下配置：

```
qiniu.kodo.access-key=
qiniu.kodo.secret-key=
qiniu.kodo.bucket-name=
# 访问域名，如：https://images.aaa.com
qiniu.kodo.domain=
# 可选项：huadong/huabei/huanan/beimei/xinjiapo，默认：autoRegion
qiniu.kodo.region=autoRegion
```

重启Torna

### 2.3 使用s3存储

`application.properties`配置文件新增如下配置：

```
s3.oss.endpoint=
s3.oss.access-key=
s3.oss.access-secret=
s3.oss.bucket-name=
# 没有可不填
s3.oss.region=
# domain格式
s3.oss.domain-pattern=https://s3.<region>.amazonaws.com/<bucketName>
# bucketName是否放在path中，如果为false（Virtual-hosted–style requests），则s3.oss.domain-pattern改成：
# https://<bucketName>.s3.<region>.amazonaws.com
s3.oss.path-style-access=true
```