@echo off
REM init_db.bat - Ejecuta el script SQL para crear y poblar la base avicola2
REM Uso: init_db.bat [usuario] [password]

if "%1"=="" (
  set USER=root
) else (
  set USER=%1
)

if "%2"=="" (
  set PASS=
) else (
  set PASS=%2
)

set SCRIPT=%~dp0..\src\main\java\com\avitech\sia\db\avicola2.sql

if "%PASS%"=="" (
  echo Ejecutando: mysql -u %USER% -p ^< "%SCRIPT%"
  mysql -u %USER% -p < "%SCRIPT%"
) else (
  echo Ejecutando: mysql -u %USER% -p******** ^< "%SCRIPT%"
  mysql -u %USER% -p%PASS% < "%SCRIPT%"
)

pause

