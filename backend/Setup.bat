@echo off
REM build.bat - Spring Boot 项目构建和运行脚本 (Windows)

REM --- 配置 ---
set APP_NAME=backend
set PROFILE=dev
set PORT=8080

REM --- 主逻辑 ---
if "%1"=="clean" goto clean
if "%1"=="compile" goto compile
if "%1"=="run" goto run
if "%1"=="debug" goto debug
if "%1"=="package" goto package
if "%1"=="all" goto all

echo Usage: %0 {clean^|compile^|run^|package^|all^|debug}
exit /b 1

:clean
echo Cleaning project...
mvn clean
goto end

:compile
echo Compiling project...
mvn compile
goto end

:run
echo Running Spring Boot application on port %PORT%...
mvn spring-boot:run
goto end

:debug
echo Running Spring Boot application in DEBUG mode on port %PORT%...
mvn spring-boot:run -X
goto end

:package
echo Packaging project...
mvn package
goto end

:all
call mvn clean
call mvn compile
call mvn spring-boot:run
goto end

:end
