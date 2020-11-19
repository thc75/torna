#!/bin/sh

echo "开始构建..."

rm -rf dist
npm run build:prod

public_path="torna-web/src/main/resources/public"

rm -rf ../$public_path/static/*
cp -r dist/* ../$public_path

echo "构建完毕"
