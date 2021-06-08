#!/bin/sh

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh

nohup java -Duser.timezone=Asia/Shanghai -jar -Xms256m -Xmx256m $app_name.jar >/dev/null 2>&1 &
