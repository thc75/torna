#!/bin/sh

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh
# --server.port：启动端口
nohup java -jar -Xms512m -Xmx512m $app_name.jar --server.port=7700 >/dev/null 2>&1 &
