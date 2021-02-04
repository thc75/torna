@echo off

set dist_dir=dist

set app_name=torna

set target_dir=%dist_dir%\%app_name%

rd /q /s %target_dir%

md %target_dir%

cd front

call build.bat

cd ..

::copy front files

md %target_dir%\dist

xcopy /e /y /q front\dist .\%target_dir%\dist

call mvn clean package

copy server\boot\target\torna.jar %target_dir%
copy server\boot\src\main\resources\application.properties %target_dir%

xcopy /e /y /q script  %target_dir%

pause