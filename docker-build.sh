#!/bin/sh

# 打包并运行docker镜像

git pull

sh release.sh

echo "开始创建docker hub镜像:latest"
docker build -t tanghc2020/torna:latest .

if [ -n "${1}" ];then
  echo "开始创建docker hub镜像:${1}"
  docker build -t tanghc2020/torna:${1} .
fi



