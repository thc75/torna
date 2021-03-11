#!/bin/sh

# 打包并运行docker镜像

# 打包
mvn clean package

# 创建镜像
docker build -t tanghc2020/torna .
