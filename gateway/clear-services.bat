@echo off
REM Kong Gateway - Clear All Services Script
REM This script removes all registered services and routes from Kong Gateway
REM Usage: clear-services.bat

setlocal enabledelayedexpansion

set KONG_ADMIN_URL=http://localhost:8001

echo.
echo Clearing all services from Kong Gateway
echo Kong Admin URL: %KONG_ADMIN_URL%
echo.

REM Check if Kong is ready
echo Checking Kong Gateway status...
curl -s %KONG_ADMIN_URL%/status >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Kong Gateway is not responding. Please check if Kong is running.
    exit /b 1
)
echo [OK] Kong Gateway is ready!
echo.

REM Remove services
echo Removing service: auth-service
curl -s -X DELETE %KONG_ADMIN_URL%/services/auth-service >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'auth-service' not found (already removed)
) else (
    echo [OK] Service 'auth-service' removed successfully
)

echo Removing service: user-service
curl -s -X DELETE %KONG_ADMIN_URL%/services/user-service >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'user-service' not found (already removed)
) else (
    echo [OK] Service 'user-service' removed successfully
)

echo Removing service: note-service
curl -s -X DELETE %KONG_ADMIN_URL%/services/note-service >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'note-service' not found (already removed)
) else (
    echo [OK] Service 'note-service' removed successfully
)

echo Removing service: realtime-collab-service
curl -s -X DELETE %KONG_ADMIN_URL%/services/realtime-collab-service >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'realtime-collab-service' not found (already removed)
) else (
    echo [OK] Service 'realtime-collab-service' removed successfully
)

echo Removing service: notification-producer
curl -s -X DELETE %KONG_ADMIN_URL%/services/notification-producer >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'notification-producer' not found (already removed)
) else (
    echo [OK] Service 'notification-producer' removed successfully
)

echo Removing service: notification-processor
curl -s -X DELETE %KONG_ADMIN_URL%/services/notification-processor >nul 2>&1
if errorlevel 1 (
    echo [INFO] Service 'notification-processor' not found (already removed)
) else (
    echo [OK] Service 'notification-processor' removed successfully
)

echo.
echo ========================================
echo All services cleared successfully!
echo ========================================
echo.

endlocal
