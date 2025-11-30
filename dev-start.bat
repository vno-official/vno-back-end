@echo off
REM Quick Start Script
REM This script starts all services in development mode
REM Usage: dev-start.bat

echo.
echo Starting VNO Backend Services in Development Mode
echo.
echo This will start all services using Gradle's quarkusDev mode
echo Each service will run in a separate Command Prompt window
echo.

REM Start all services in separate windows
echo Starting Auth Service on port 8080...
start "Auth Service - Port 8080" cmd /k "cd auth-service && gradlew quarkusDev"
timeout /t 2 /nobreak >nul

echo Starting User Service on port 8081...
start "User Service - Port 8081" cmd /k "cd user-service && gradlew quarkusDev"
timeout /t 2 /nobreak >nul

echo Starting Note Service on port 8082...
start "Note Service - Port 8082" cmd /k "cd note-service && gradlew quarkusDev"
timeout /t 2 /nobreak >nul

echo Starting Realtime Collab Service on port 8083...
start "Realtime Collab Service - Port 8083" cmd /k "cd realtime-collab-service && gradlew quarkusDev"
timeout /t 2 /nobreak >nul

echo Starting Notification Producer on port 8084...
start "Notification Producer - Port 8084" cmd /k "cd notification-service\producer && gradlew quarkusDev"
timeout /t 2 /nobreak >nul

echo Starting Notification Processor on port 8085...
start "Notification Processor - Port 8085" cmd /k "cd notification-service\processor && gradlew quarkusDev"

echo.
echo ========================================
echo All services are starting in separate windows
echo ========================================
echo.
echo Service endpoints:
echo   - Auth Service:           http://localhost:8080
echo   - User Service:           http://localhost:8081
echo   - Note Service:           http://localhost:8082
echo   - Realtime Collab:        http://localhost:8083
echo   - Notification Producer:  http://localhost:8084
echo   - Notification Processor: http://localhost:8085
echo.
echo To use Kong Gateway:
echo   1. Start Kong: cd gateway ^&^& docker-compose up -d
echo   2. Register services: cd gateway ^&^& register-services-local.bat
echo.
echo Close each window to stop the services
echo.
