#!/bin/sh

# 执行方式：sh docker-build-push.sh <版本号>
# 如：sh docker-build-push.sh 1.15.2

git pull

sh release.sh

echo "开始创建docker hub镜像:latest"
docker build -f Dockerfile.local -t tanghc2020/torna:latest .

sleep 1

echo "推送镜像到docker hub:latest，执行命令：docker push tanghc2020/torna:latest"
docker push tanghc2020/torna:latest

sleep 1

# 如果有参数
if [ -n "${1}" ];then
  echo "开始创建docker hub镜像:${1}"
  docker build -f Dockerfile.local -t tanghc2020/torna:${1} .

  sleep 1

  echo "推送镜像到docker hub:${1}，执行命令：docker push tanghc2020/torna:${1}"
  docker push tanghc2020/torna:${1}
fi
