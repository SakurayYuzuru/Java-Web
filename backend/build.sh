#!/bin/bash
# build.sh - Spring Boot 项目构建和运行脚本

# --- 配置 ---
APP_NAME="backend"
PROFILE="dev"
PORT=8080

# --- 函数 ---
function clean() {
    echo "Cleaning project..."
    mvn clean
}

function compile() {
    echo "Compiling project..."
    mvn compile
}

function run() {
    echo "Running Spring Boot application on port $PORT..."
    mvn spring-boot:run
}

function debug() {
    echo "Running Spring Boot application on port $PORT..."
    mvn spring-boot:run -X
}

function package_app() {
    echo "Packaging project..."
    mvn package
}

# --- 主逻辑 ---
case "$1" in
    clean)
        clean
        ;;
    compile)
        compile
        ;;
    run)
        run
        ;;
    package)
        package_app
        ;;
    all)
        clean
        compile
        run
        ;;
    debug)
        debug
        ;;
    *)
        echo "Usage: $0 {clean|compile|run|package|all}"
        exit 1
        ;;
esac
