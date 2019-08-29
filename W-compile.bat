@echo off
setlocal

pushd "%~dp0"

set x=0

echo compile Main
javac -d ./class -encoding UTF-8 ./Main.java

if not %errorlevel% == 0 (
    call :fail
)

for /d %%d in (./gamedata/*) do (
    echo compile %%d
    javac -d ./class -encoding UTF-8 ./gamedata/%%d/*.java

    if not !errorlevel! == 0 (
        call :fail
    )
)

popd

if "%1" == "" (
    pause
)

exit /b x

:fail
set x=1
exit /b

rem コンパイルする。

rem 引数なし（直接実行）だと停止。
rem 引数あり（呼び出し）だと、コンパイル成功 or 失敗 (0 or 1) を返す。
