#!/bin/sh

# 构建docker并推送到阿里云
# 执行方式：sh docker-build-push.sh <版本号>
# 如：sh docker-build-push.sh 1.15.2

git pull

sh release.sh

echo "开始创建docker hub镜像:latest"
docker build -f Dockerfile.local -t registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest .

sleep 1

echo "推送镜像到docker hub:latest，执行命令：docker push registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest"
docker push registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest

sleep 1

# 如果有参数
if [ -n "${1}" ];then

  echo "tag 镜像，命令：docker tag registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${1}"
  docker tag registry.cn-hangzhou.aliyuncs.com/tanghc/torna:latest registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${1}

  sleep 1

  echo "推送镜像到docker hub:${1}，执行命令：docker push registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${1}"
  docker push registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${1}
fi
