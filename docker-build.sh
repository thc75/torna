#!/bin/sh

git pull

# 打包并运行docker镜像

# 先执行前端构建
cd front

sh build.sh

cd ..

# ----

mvn clean package

# 创建镜像
docker build -t tanghc2020/torna .

# 将application.properties文件放在宿主机的/etc/torna/config下，然后执行下面这句
# docker run --name torna -p 7700:7700 -v /opt/torna/config:/torna/config -d <IMAGE ID>

