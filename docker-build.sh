#!/bin/sh

# 打包docker镜像

git pull

sh release.sh

echo "开始创建docker hub镜像:latest"
docker build -f Dockerfile.local -t tanghc2020/torna:latest .
