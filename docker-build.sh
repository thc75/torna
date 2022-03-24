#!/bin/sh

# 打包并运行docker镜像

git pull

sh release.sh

echo "开始创建docker hub镜像"
docker build -t tanghc2020/torna .

