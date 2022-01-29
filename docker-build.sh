#!/bin/sh

# 打包并运行docker镜像

git pull

sh release.sh

# 创建镜像
docker build -t tanghc2020/torna .
