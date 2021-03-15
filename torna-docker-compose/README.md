使用docker-compose部署torna

# 环境要求
- docker 1.8+
- docker-compose 1.12+
- mysql 5.7+
# 准备
主要是初始化数据库和导入数据。直接下载项目的mysql脚本导入即可
# docker-compose安装
下面介绍docker-compose的安装。
## 下载docker-compose
```
https://github-production-release-asset-2e65be.s3.amazonaws.com/15045751/6e19c880-7b13-11ea-97d7-bec401ece2d4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20200527%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20200527T140431Z&X-Amz-Expires=300&X-Amz-Signature=6fa6e2efd7f3e53f608b28e70c0e756274e6439cb28b491b9f0aaf269429054d&X-Amz-SignedHeaders=host&actor_id=9592228&repo_id=15045751&response-content-disposition=attachment%3B%20filename%3Ddocker-compose-Linux-x86_64&response-content-type=application%2Foctet-stream
```
上面的连接可能会失效，如失效则请直接访问github找到对应的版本。
```
https://github.com/docker/compose/releases
```
下载后重名为docker-compose。如果你在windows上下载也可以重命名后传到linux服务器上。
然后将docker-compose放到`/usr/local/bin/`目录下。然后执行下列命令完成安装。
```
sudo chmod +x /usr/local/bin/docker-compose
```
安装完成后可以执行下面命令检查
```
docker-compose --version
```
# 部署torna
在部署torna的服务器上创建一个torna的目录用于放置部署配置。
## 创建docker-compose.yaml编排文件
编排文件内容参考如下：
```
version: '3'
services:
  torna:
    image: tanghc2020/torna:latest
    container_name: torna
    ports:
      - 7700:7700
    volumes:
      - ./application.properties:/torna/config/application.properties
    environment:
      - TZ=Asia/Shanghai
      - JAVA_OPTS=-server -Xmx512m -Xms512m -Djava.awt.headless=true
    networks:
      - torna-net
networks:
  torna-net:
```
volumes中的配置表示把本地的配置挂载到torna容器中。可以挂载多个文件。
容器中挂载的配置路径为/torna/config`。上面配置中暴露的端口是7700。
## 创建application.properties
application.properties源自`server/boot/src/main/resources/application.properties`,因此实际使用过程中请参考最新的配置来更新，
下面主要是做说明和参考
```
# 服务端口
server.port=7700

# 数据库连接配置
mysql.host=localhost:3306
mysql.username=root
mysql.password=root
```
主要是修改application.properties中的数据库配置为自己的数据库。

当然如果你的服务器可以连接外部网络，那么上面的过程你完全可以直接如命令下载到服务器：
```
mkdir /data/torna-docker-compose
cd /data/torna-docker-compose
wget https://gitee.com/durcframework/torna/raw/master/server/boot/src/main/resources/application.properties
wget https://gitee.com/durcframework/torna/raw/master/torna-docker-compose/docker-compose.yml
```
下载完后修改application.properties配置即可

## 启动和停止容器
完成上面的配置后，就可以启动torna。

启动
```
docker-compose up -d
```
停止
```
docker-compose down
```
**注意：** 执行启动或者是停止命令都需要进入到torna部署目录，
即必须是在docker-compose.yaml所在目录下执行。

## 登陆torna
http://ip:7700/
>登录账号相关信息直接查看项目的顶层readme文档。
## 查看日志
如果你需要观察sql语句在torna中的执行情况。通过docker logs来查看。

```
# 查询出torna容器的id
docker ps|grep torna
# 根据容器id查看日志
docker logs -f [容器id]
```

