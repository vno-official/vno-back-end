@echo off
REM Stop All VNO Service Containers

echo.
echo ========================================
echo Stopping All VNO Services
echo ========================================
echo.

set "services=auth-service user-service note-service realtime-collab-service notification-producer notification-processor"

for %%s in (%services%) do (
    echo Stopping vno-%%s-container...
    docker stop vno-%%s-container >nul 2>&1
    if errorlevel 1 (
        echo [INFO] Container vno-%%s-container not running
    ) else (
        echo [OK] Stopped vno-%%s-container
    )
    
    echo Removing vno-%%s-container...
    docker rm vno-%%s-container >nul 2>&1
    if not errorlevel 1 (
        echo [OK] Removed vno-%%s-container
    )
    echo.
)

echo ========================================
echo All Services Stopped
echo ========================================
echo.
