#!/bin/sh

sh docker-build.sh

sleep 1

echo "推送镜像到docker hub"
docker push tanghc2020/torna:latest
