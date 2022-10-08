###### 构建前端 ######
FROM node:12.14 as build_front

WORKDIR /static/front

# 复制前端文件到app下
COPY front /static/front

RUN npm install

RUN npm run build:prod


###### 构建后端 ######
# 二开推荐阅读[如何提高项目构建效率](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/scene/build/speed.html)
# 选择构建用基础镜像。如需更换，请到[dockerhub官方仓库](https://hub.docker.com/_/java?tab=tags)自行选择后替换。
FROM maven:3.6.0-jdk-8-slim as build

# 指定构建过程中的工作目录
WORKDIR /torna

# 将src目录下所有文件，拷贝到工作目录中src目录下（.gitignore/.dockerignore中文件除外）
COPY server /torna/server

# 将pom.xml文件，拷贝到工作目录下
COPY settings.xml pom.xml /torna/

# 执行代码编译命令
# 自定义settings.xml, 选用国内镜像源以提高下载速度
RUN mvn -s /torna/settings.xml -f /torna/pom.xml clean package


# 选择运行时基础镜像
FROM openjdk:8

VOLUME /tmp
# 创建文件夹，读取配置文件，可将此目录挂载映射到宿主机目录
# 表示读取宿主机/etc/torna/application.properties文件
# 启动docker加参数：-v /etc/torna:/torna/config
RUN mkdir -p /torna/config

# 指定运行时的工作目录
WORKDIR /torna

# 将构建产物前端dist文件夹拷贝到运行时目录中
COPY --from=build_front /static/front/dist /torna/dist

# 将构建产物jar包拷贝到运行时目录中
COPY --from=build /torna/server/boot/target/torna.jar .

# 暴露端口
# 此处端口必须与「服务设置」-「流水线」以及「手动上传代码包」部署时填写的端口一致，否则会部署失败。
EXPOSE 7700

# startup
ENTRYPOINT [ "sh", "-c", "java -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -jar /torna/torna.jar" ]