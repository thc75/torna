#!/bin/sh

# 打包并运行docker镜像

# 先执行前端构建
cd front

sh build.sh

cd ..

# ----

mvn clean package

# 创建镜像
docker build -t tanghc2020/torna .
