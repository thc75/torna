###### 构建前端 ######
FROM node:12.14 as build_front

WORKDIR /static/front

# 复制前端文件到app下
COPY front /static/front

RUN npm install --legacy-peer-deps

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
FROM alpine:3.13

VOLUME /tmp
RUN mkdir -p /torna/config

# 容器默认时区为UTC，如需使用上海时间请启用以下时区设置命令
RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone

# 使用 HTTPS 协议访问容器云调用证书安装
RUN apk add ca-certificates

# 安装依赖包，如需其他依赖包，请到alpine依赖包管理(https://pkgs.alpinelinux.org/packages?name=php8*imagick*&branch=v3.13)查找。
# 选用国内镜像源以提高下载速度
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tencent.com/g' /etc/apk/repositories \
    && apk add --update --no-cache openjdk8-jre-base \
    && rm -f /var/cache/apk/*

# 指定运行时的工作目录
WORKDIR /torna

# 将构建产物前端dist文件夹拷贝到运行时目录中
COPY --from=build_front /static/front/dist /torna/dist

# 将构建产物jar包拷贝到运行时目录中
COPY --from=build /torna/server/boot/target/torna.jar .

# 暴露端口
# 此处端口必须与「服务设置」-「流水线」以及「手动上传代码包」部署时填写的端口一致，否则会部署失败。
EXPOSE 7700

# set jvm
ENV JAVA_OPTS="-server -Xmx512m -Xms512m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -jar /torna/torna.jar --spring.config.location=/torna/config/application.properties" ]