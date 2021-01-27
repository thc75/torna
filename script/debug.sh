#!/bin/sh

# 远程调试启动这个

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh
# --server.port：启动端口, 5005: 远程调试端口
nohup java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005\
 -Xms256m -Xmx256m $app_name.jar >/dev/null 2>&1 &
