@echo off
set "LOCAL_MVN=%~dp0tools\maven\bin\mvn.cmd"
echo Uruchamiam Panel Pracodawcy przy uzyciu lokalnego Mavena...
"%LOCAL_MVN%" javafx:run -Djavafx.mainClass=org.example.PracodawcaApp
if %errorlevel% neq 0 pause
