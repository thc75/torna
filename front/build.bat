@echo off

rd /q /s dist

call npm install && npm run build:prod
