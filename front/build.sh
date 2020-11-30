#!/bin/sh

echo "开始构建前端..."

rm -rf dist
npm run build:prod

public_path="start/src/main/resources/static"

rm -rf ../$public_path/static/*
cp -r dist/* ../$public_path

echo "前端构建完毕"
