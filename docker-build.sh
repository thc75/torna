#!/bin/sh

# 打包docker镜像

git pull

sh release.sh

echo "开始创建阿里云docker镜像:latest"
docker build -f Dockerfile.local -t registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest .
