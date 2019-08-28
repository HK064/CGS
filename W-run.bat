@echo off
cd /d %~dp0

java -classpath ./class/ Main

if "%1" == "" (
    pause
)

exit /b

rem 実行する。

rem 引数なし（直接実行）だと停止。
rem 引数あり（呼び出し）だと停止せず終了。
