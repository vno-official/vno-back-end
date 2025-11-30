@echo off
REM Build All Docker Images Script
REM This script builds all VNO service Docker images
REM Usage: build-all-images.bat

setlocal enabledelayedexpansion

echo.
echo Building All VNO Service Docker Images
echo.

REM Check if Docker is running
echo Checking Docker status...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop.
    exit /b 1
)
echo [OK] Docker is running
echo.

REM Record start time
set start_time=%time%

REM Function to build a Docker image
REM Parameters: ServiceName, ServicePath, ImageName, DockerfilePath

echo Building Auth Service...
cd auth-service
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build Auth Service image
    cd ..
    exit /b 1
)
echo [OK] Auth Service image built successfully
cd ..
echo.

echo Building User Service...
cd user-service
docker build -f src/main/docker/Dockerfile.jvm -t vno-user-service:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build User Service image
    cd ..
    exit /b 1
)
echo [OK] User Service image built successfully
cd ..
echo.

echo Building Note Service...
cd note-service
docker build -f src/main/docker/Dockerfile.jvm -t vno-note-service:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build Note Service image
    cd ..
    exit /b 1
)
echo [OK] Note Service image built successfully
cd ..
echo.

echo Building Realtime Collab Service...
cd realtime-collab-service
docker build -f src/main/docker/Dockerfile.jvm -t vno-realtime-collab-service:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build Realtime Collab Service image
    cd ..
    exit /b 1
)
echo [OK] Realtime Collab Service image built successfully
cd ..
echo.

echo Building Notification Producer...
cd notification-service
docker build -f producer/src/main/docker/Dockerfile.jvm -t vno-notification-producer:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build Notification Producer image
    cd ..
    exit /b 1
)
echo [OK] Notification Producer image built successfully
cd ..
echo.

echo Building Notification Processor...
cd notification-service
docker build -f processor/src/main/docker/Dockerfile.jvm -t vno-notification-processor:latest . >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Failed to build Notification Processor image
    cd ..
    exit /b 1
)
echo [OK] Notification Processor image built successfully
cd ..
echo.

echo ========================================
echo All Docker images built successfully!
echo ========================================
echo.
echo Built images:
echo   - vno-auth-service:latest
echo   - vno-user-service:latest
echo   - vno-note-service:latest
echo   - vno-realtime-collab-service:latest
echo   - vno-notification-producer:latest
echo   - vno-notification-processor:latest
echo.
echo Next steps:
echo   1. Start Kong Gateway: cd gateway ^&^& docker-compose up -d
echo   2. Register services: cd gateway ^&^& register-services-docker.bat
echo.

endlocal
