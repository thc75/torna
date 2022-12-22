#!/bin/sh

# 执行文件名称
app_name="torna"

# 先关闭服务
sh shutdown.sh

JAVA_OPTS=""

if [ -n "${1}" ];then
  JAVA_OPTS="${1}"
fi

nohup java -server -Duser.timezone=Asia/Shanghai -jar -Xms512m -Xmx512m $JAVA_OPTS $app_name.jar >/dev/null 2>&1 &
