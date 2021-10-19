#!/bin/sh

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh

nohup java -Duser.timezone=Asia/Shanghai -jar -Xms512m -Xmx512m $app_name.jar >/dev/null 2>&1 &
