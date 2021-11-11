@echo off

set dist_dir=dist

set app_name=torna

set version="1.11.0"

set build_folder=%app_name%-%version%

set target_dir=%dist_dir%\%build_folder%

cd front

call build.bat

cd ..

call mvn clean package

::copy files

rd /q /s %target_dir%

md %target_dir%

md %target_dir%\dist

xcopy /e /y /q front\dist .\%target_dir%\dist

copy server\boot\target\torna.jar %target_dir%
copy server\boot\src\main\resources\application.properties %target_dir%

xcopy /e /y /q script  %target_dir%

pause