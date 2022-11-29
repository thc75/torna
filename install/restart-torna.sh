#!/bin/sh

# 说明：下载最新版本Torna并重启
# 前提：需要将配置文件放在`/etc/torna/application.properties`
# 保存文件名：restart-torna.sh
# 运行方式：sh restart-torna.sh <version>
#
# 如重启1.18.2版本：sh restart-torna.sh 1.18.2

version=""

if [ -n "${1}" ];then
  version="${1}"
else
  echo "请输入版本号，如：sh restart-torna.sh 1.18.2"
  exit 1
fi

file_name="torna-${version}.zip"

folder_name="torna-${version}"

if [ -f "./$file_name" ];then
  echo "${file_name}已存在跳过下载"
else
  url="https://gitee.com/durcframework/torna/releases/download/v${version}/${file_name}"

  wget $url

  unzip $file_name
fi

cd $folder_name

echo "启动Torna"

# 重启并指定配置文件。
# startup.sh脚本第一个参数可以写JVM参数
sh startup.sh -Dspring.config.location=/etc/torna/application.properties