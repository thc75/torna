#!/bin/sh

echo "开始构建前端..."

rm -rf dist

npm install --registry=https://registry.npm.taobao.org
npm run build:prod
