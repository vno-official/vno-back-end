@echo off
REM Kong Gateway Service Registration Script - Docker Container
REM This script registers all VNO services to Kong Gateway running in Docker
REM Usage: register-services-docker.bat

setlocal enabledelayedexpansion

set KONG_ADMIN_URL=http://localhost:8001

echo.
echo Registering VNO Services to Kong Gateway (Docker Container)
echo Kong Admin URL: %KONG_ADMIN_URL%
echo.

REM Wait for Kong to be ready
echo Waiting for Kong Gateway to be ready...
set /a retryCount=0
set /a maxRetries=30

:check_kong
curl -s %KONG_ADMIN_URL%/status >nul 2>&1
if errorlevel 1 (
    set /a retryCount+=1
    if !retryCount! geq %maxRetries% (
        echo [ERROR] Kong Gateway is not responding. Please check if Kong is running.
        exit /b 1
    )
    echo Attempt !retryCount!/%maxRetries% - Kong not ready yet...
    timeout /t 2 /nobreak >nul
    goto check_kong
)

echo [OK] Kong Gateway is ready!
echo.

REM Register Auth Service
echo Registering service: auth-service
curl -s -X PUT %KONG_ADMIN_URL%/services/auth-service -d "name=auth-service" -d "url=http://vno-auth-service:8080" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register auth-service
) else (
    echo [OK] Service 'auth-service' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/auth-service/routes/auth-service-route -d "name=auth-service-route" -d "paths[]=/api/auth" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for auth-service
) else (
    echo [OK] Route '/api/auth' registered successfully
)
echo.

REM Register User Service
echo Registering service: user-service
curl -s -X PUT %KONG_ADMIN_URL%/services/user-service -d "name=user-service" -d "url=http://vno-user-service:8081" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register user-service
) else (
    echo [OK] Service 'user-service' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/user-service/routes/user-service-route -d "name=user-service-route" -d "paths[]=/api/users" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for user-service
) else (
    echo [OK] Route '/api/users' registered successfully
)
echo.

REM Register Note Service
echo Registering service: note-service
curl -s -X PUT %KONG_ADMIN_URL%/services/note-service -d "name=note-service" -d "url=http://vno-note-service:8082" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register note-service
) else (
    echo [OK] Service 'note-service' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/note-service/routes/note-service-route -d "name=note-service-route" -d "paths[]=/api/notes" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for note-service
) else (
    echo [OK] Route '/api/notes' registered successfully
)
echo.

REM Register Realtime Collab Service
echo Registering service: realtime-collab-service
curl -s -X PUT %KONG_ADMIN_URL%/services/realtime-collab-service -d "name=realtime-collab-service" -d "url=http://vno-realtime-collab-service:8083" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register realtime-collab-service
) else (
    echo [OK] Service 'realtime-collab-service' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/realtime-collab-service/routes/realtime-collab-service-route -d "name=realtime-collab-service-route" -d "paths[]=/api/collab" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for realtime-collab-service
) else (
    echo [OK] Route '/api/collab' registered successfully
)
echo.

REM Register Notification Producer
echo Registering service: notification-producer
curl -s -X PUT %KONG_ADMIN_URL%/services/notification-producer -d "name=notification-producer" -d "url=http://vno-notification-producer:8084" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register notification-producer
) else (
    echo [OK] Service 'notification-producer' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/notification-producer/routes/notification-producer-route -d "name=notification-producer-route" -d "paths[]=/api/notifications" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for notification-producer
) else (
    echo [OK] Route '/api/notifications' registered successfully
)
echo.

REM Register Notification Processor
echo Registering service: notification-processor
curl -s -X PUT %KONG_ADMIN_URL%/services/notification-processor -d "name=notification-processor" -d "url=http://vno-notification-processor:8085" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register notification-processor
) else (
    echo [OK] Service 'notification-processor' registered successfully
)

curl -s -X PUT %KONG_ADMIN_URL%/services/notification-processor/routes/notification-processor-route -d "name=notification-processor-route" -d "paths[]=/api/notifications/processor" -d "strip_path=false" >nul
if errorlevel 1 (
    echo [ERROR] Failed to register route for notification-processor
) else (
    echo [OK] Route '/api/notifications/processor' registered successfully
)
echo.

echo ========================================
echo All services registered successfully!
echo ========================================
echo.
echo Service endpoints available at:
echo   - Auth Service:         http://localhost:8000/api/auth
echo   - User Service:         http://localhost:8000/api/users
echo   - Note Service:         http://localhost:8000/api/notes
echo   - Realtime Collab:      http://localhost:8000/api/collab
echo   - Notifications:        http://localhost:8000/api/notifications
echo   - Notifications Proc:   http://localhost:8000/api/notifications/processor
echo.
echo Kong Admin GUI:        http://localhost:8002
echo Kong Admin API:        http://localhost:8001
echo.

endlocal
