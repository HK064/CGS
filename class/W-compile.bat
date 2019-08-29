@echo off
echo compile Main
javac -d ./class -encoding UTF-8 ./Main.java
for /d %%a in (./gamedata/*) do (
    echo compile %%a
    javac -d ./class -encoding UTF-8 ./gamedata/%%a/*.java
)

pause
