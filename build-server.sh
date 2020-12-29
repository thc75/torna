#!/bin/sh

# 服务端文件夹名称
server_folder_name="start"
# 构建目录
dist_dir="dist"
# 执行文件名称
app_name="torna"
# 输出目录
target_dir="$dist_dir/$app_name"

if [ ! -d "$target_dir" ]; then
  mkdir -p $target_dir
fi

rm -rf $target_dir/*

echo "开始构建服务端..."

mvn clean package

echo "复制服务端文件到$target_dir"

cp -r $server_folder_name/target/*.jar $target_dir/$app_name.jar
cp -r script/* $target_dir

#echo "打成zip包"
#cd $dist_dir
#zip -r -q "$app_name.zip" $app_name

echo "服务端构建完毕，构建结果在${target_dir}文件夹下"