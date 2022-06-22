#!/bin/sh

# 打包并运行docker镜像

git pull

sh release.sh

echo "开始创建docker hub镜像"
docker build -t tanghc2020/torna .

sleep 1

echo "开始创建docker hub镜像:${1}"
docker build -t tanghc2020/torna:${1} .

sleep 1

echo "推送镜像到docker hub:latest"
docker push tanghc2020/torna:latest

sleep 1

# 如果有参数
if [ -n "${1}" ];then
  echo "推送镜像到docker hub:${1}"
  docker push tanghc2020/torna:${1}
fi
