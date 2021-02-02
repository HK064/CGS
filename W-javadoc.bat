@echo off
setlocal

pushd "%~dp0"

set x=source
set y=

for /d %%d in (./source/*) do (
    set y=source.%%d
    call :add
)

for /d %%d in (./gamedata/*) do (
    set y=gamedata.%%d
    call :add
)

javadoc -d javadoc -encoding UTF-8 -charset UTF-8 -private %x%

popd

pause

exit /b

:add
set x=%x% %y%
exit /b
