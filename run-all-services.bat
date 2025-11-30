@echo off
REM Run All VNO Services in Docker Containers
REM Each service runs in detached mode with automatic port mapping

setlocal enabledelayedexpansion

echo.
echo ========================================
echo Starting All VNO Services in Docker
echo ========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop.
    exit /b 1
)

echo [OK] Docker is running
echo.

REM Function to start a service
set "services=auth-service:8080 user-service:8081 note-service:8082 realtime-collab-service:8083 notification-producer:8084 notification-processor:8085"

for %%s in (%services%) do (
    for /f "tokens=1,2 delims=:" %%a in ("%%s") do (
        set "service_name=%%a"
        set "port=%%b"
        
        echo Starting !service_name! on port !port!...
        
        REM Check if container already exists
        docker ps -a --filter "name=vno-!service_name!-container" --format "{{.Names}}" | findstr "vno-!service_name!-container" >nul 2>&1
        if not errorlevel 1 (
            echo [INFO] Container vno-!service_name!-container already exists. Removing...
            docker rm -f vno-!service_name!-container >nul 2>&1
        )
        
        REM Start container in detached mode
        docker run -d -p !port!:!port! --name vno-!service_name!-container vno-!service_name!:latest >nul 2>&1
        
        if errorlevel 1 (
            echo [ERROR] Failed to start !service_name!
        ) else (
            echo [OK] !service_name! started on port !port!
        )
        echo.
    )
)

echo ========================================
echo All Services Started
echo ========================================
echo.
echo Service endpoints:
echo   - Auth Service:         http://localhost:8080
echo   - User Service:         http://localhost:8081
echo   - Note Service:         http://localhost:8082
echo   - Realtime Collab:      http://localhost:8083
echo   - Notification Producer: http://localhost:8084
echo   - Notification Processor: http://localhost:8085
echo.
echo To view running containers:
echo   docker ps
echo.
echo To view logs:
echo   docker logs vno-auth-service-container
echo.
echo To stop all services:
echo   stop-all-services.bat
echo.

endlocal
