@echo off
setlocal

pushd "%~dp0"

call W-compile.bat 1

if %errorlevel% == 0 (
    call W-run.bat 1
)

popd

pause
