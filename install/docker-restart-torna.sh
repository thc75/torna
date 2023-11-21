#!/bin/sh

# 说明：停止并删除当前torna容器，并拉取最新版本镜像重启
# 保存文件名：docker-restart-torna.sh
# 运行方式：sh docker-restart-torna.sh [version]
#
# 参数version可选，不填默认为latest，推荐手动指定版本避免拉取到的latest还是老版本
# 如重启1.17.2版本：sh docker-restart-torna.sh 1.17.2

version="latest"

if [ -n "${1}" ];then
  version="${1}"
fi

echo "stop torna container"
docker stop torna

echo "remove torna container"
docker rm torna

echo "pull new torna image : tanghc2020/torna:${version}"
docker pull registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${version}

echo "run docker image : registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${version}"

docker run --name torna --restart=always \
  -e JAVA_OPTS="-server -Xms512m -Xmx512m" \
  -v /etc/torna/application.properties:/torna/config/application.properties \
  -d registry.cn-hangzhou.aliyuncs.com/tanghc/torna:${version}
