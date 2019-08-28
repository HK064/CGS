@echo off
cd /d %~dp0

call W-compile.bat 1

if %errorlevel% == 0 (
    call W-run.bat 1
)

pause

rem コンパイルして、成功したら実行する。
