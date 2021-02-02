@echo off
setlocal

pushd "%~dp0"

java -classpath ./class/ Main

popd

if "%1" == "" (
    pause
)

exit /b
