@echo off
IF %1 == commit (
cd ..\ROSE
git commit -m %2
cd ..\RoseApp
git commit -m %3
GOTO :eof
)
@echo on
cd ..\ROSE
git %1 %2 %3 %4
cd ..\RoseApp
git %1 %2 %3 %4
:eof