@echo off
start "" java -jar cheque-app-1.0.jar
timeout /t 5 > nul
start http://localhost:8103/cheque/formulaire