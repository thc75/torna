#!/bin/sh

echo "开始构建前端..."

rm -rf dist

npm install

npm run build:prod
