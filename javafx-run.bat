@echo off
set "LOCAL_MVN=%~dp0tools\maven\bin\mvn.cmd"
echo Uruchamiam projekt przy uzyciu lokalnego Mavena...
"%LOCAL_MVN%" javafx:run
if %errorlevel% neq 0 pause
