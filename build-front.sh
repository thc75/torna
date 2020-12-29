#!/bin/sh

# 构建目录
dist_dir="dist"
# 服务端文件夹名称
# 执行文件名称
app_name="torna"

# 输出目录
target_dir="$dist_dir/$app_name"

if [ ! -d "$target_dir" ]; then
  mkdir -p $target_dir
fi

rm -rf $target_dir/*

# 先执行前端构建
cd front

npm install

sh build.sh

cd ..

# 复制前端资源

echo "复制前端文件到$target_dir"

cp -r front/dist ./$target_dir

echo "前端构建完毕，构建结果在 ${target_dir}/dist 文件夹下"