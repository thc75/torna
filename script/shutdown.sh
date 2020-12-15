#!/bin/sh

# 执行文件名称
app_name="torna"

pid=$(ps -ef | grep $app_name.jar | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
  echo "stop $app_name.jar, pid:$pid"
  kill -9 "$pid"
fi
