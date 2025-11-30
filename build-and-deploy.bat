@echo off
REM Build and Deploy Script
REM This script builds all services and creates Docker images in one command
REM Usage: build-and-deploy.bat

setlocal enabledelayedexpansion

echo.
echo VNO Backend - Build and Deploy All Services
echo.

REM Step 1: Clean all
echo ========================================
echo Step 1/3: Cleaning all services...
echo ========================================
call gradlew cleanAll
if errorlevel 1 (
    echo [ERROR] Clean failed
    exit /b 1
)
echo [OK] Clean completed
echo.

REM Step 2: Build all
echo ========================================
echo Step 2/3: Building all services...
echo ========================================
call gradlew buildAll -x test
if errorlevel 1 (
    echo [ERROR] Build failed
    exit /b 1
)
echo [OK] Build completed
echo.

REM Step 3: Build Docker images
echo ========================================
echo Step 3/3: Building Docker images...
echo ========================================
call build-all-images.bat
if errorlevel 1 (
    echo [ERROR] Docker build failed
    exit /b 1
)

echo.
echo ========================================
echo All services built and dockerized successfully!
echo ========================================
echo.
echo Summary:
echo   [OK] All services cleaned
echo   [OK] All services built
echo   [OK] All Docker images created
echo.
echo Next steps:
echo   1. Start Kong Gateway: cd gateway ^&^& docker-compose up -d
echo   2. Register services: cd gateway ^&^& register-services-docker.bat
echo   3. Test services: curl http://localhost:8000/api/auth/health
echo.

endlocal
