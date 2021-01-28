#!/bin/sh

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh

nohup java -jar -XX:SurvivorRatio=6 -Xmn100m -Xms256m -Xmx256m $app_name.jar >/dev/null 2>&1 &
