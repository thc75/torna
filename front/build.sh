#!/bin/sh

echo "开始构建前端..."

rm -rf dist
npm run build:prod

echo "前端构建完毕"
