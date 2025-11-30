@echo off
setlocal enabledelayedexpansion

REM VNO Docker Services Manager
REM Interactive script to start/stop Docker containers

:main_menu
cls
echo.
echo ========================================
echo    VNO Docker Services Manager
echo ========================================
echo.
echo 1. Start a service
echo 2. Stop a service
echo 3. View running containers
echo 4. View logs
echo 5. Exit
echo.
set /p "action=Choose an action (1-5): "

if "%action%"=="1" goto start_service
if "%action%"=="2" goto stop_service
if "%action%"=="3" goto view_containers
if "%action%"=="4" goto view_logs
if "%action%"=="5" goto end
echo Invalid choice. Please try again.
timeout /t 2 >nul
goto main_menu

:start_service
cls
echo.
echo ========================================
echo    Start a Service
echo ========================================
echo.
echo Available services:
echo.
echo 1. Auth Service (Port 8080)
echo 2. User Service (Port 8081)
echo 3. Note Service (Port 8082)
echo 4. Realtime Collab Service (Port 8083)
echo 5. Notification Producer (Port 8084)
echo 6. Notification Processor (Port 8085)
echo 7. All Services
echo 8. Back to main menu
echo.
set /p "service=Choose a service to start (1-8): "

if "%service%"=="1" (
    set "service_name=auth-service"
    set "port=8080"
    goto do_start
)
if "%service%"=="2" (
    set "service_name=user-service"
    set "port=8081"
    goto do_start
)
if "%service%"=="3" (
    set "service_name=note-service"
    set "port=8082"
    goto do_start
)
if "%service%"=="4" (
    set "service_name=realtime-collab-service"
    set "port=8083"
    goto do_start
)
if "%service%"=="5" (
    set "service_name=notification-producer"
    set "port=8084"
    goto do_start
)
if "%service%"=="6" (
    set "service_name=notification-processor"
    set "port=8085"
    goto do_start
)
if "%service%"=="7" goto start_all
if "%service%"=="8" goto main_menu

echo Invalid choice. Please try again.
timeout /t 2 >nul
goto start_service

:do_start
echo.
echo Starting %service_name% on port %port%...
echo.

REM Check if container already exists
docker ps -a --filter "name=vno-%service_name%-container" --format "{{.Names}}" | findstr "vno-%service_name%-container" >nul 2>&1
if not errorlevel 1 (
    echo [INFO] Container vno-%service_name%-container already exists. Removing...
    docker rm -f vno-%service_name%-container >nul 2>&1
)

REM Start container in detached mode
docker run -d -p %port%:%port% --name vno-%service_name%-container vno-%service_name%:latest

if errorlevel 1 (
    echo [ERROR] Failed to start %service_name%
    echo.
    echo Make sure the Docker image exists. Build it with:
    echo   gradlew :%service_name%:build
    echo   docker build -f %service_name%/src/main/docker/Dockerfile.jvm -t vno-%service_name%:latest %service_name%
) else (
    echo [OK] %service_name% started successfully on port %port%
    echo.
    echo Access at: http://localhost:%port%
    echo View logs: docker logs -f vno-%service_name%-container
)

echo.
pause
goto main_menu

:start_all
echo.
echo Starting all services...
echo.

set "services=auth-service:8080 user-service:8081 note-service:8082 realtime-collab-service:8083 notification-producer:8084 notification-processor:8085"

for %%s in (%services%) do (
    for /f "tokens=1,2 delims=:" %%a in ("%%s") do (
        set "svc_name=%%a"
        set "svc_port=%%b"
        
        echo Starting !svc_name! on port !svc_port!...
        
        REM Check if container already exists
        docker ps -a --filter "name=vno-!svc_name!-container" --format "{{.Names}}" | findstr "vno-!svc_name!-container" >nul 2>&1
        if not errorlevel 1 (
            docker rm -f vno-!svc_name!-container >nul 2>&1
        )
        
        REM Start container in detached mode
        docker run -d -p !svc_port!:!svc_port! --name vno-!svc_name!-container vno-!svc_name!:latest >nul 2>&1
        
        if errorlevel 1 (
            echo [ERROR] Failed to start !svc_name!
        ) else (
            echo [OK] !svc_name! started on port !svc_port!
        )
    )
)

echo.
echo All services started!
echo.
pause
goto main_menu

:stop_service
cls
echo.
echo ========================================
echo    Stop a Service
echo ========================================
echo.
echo Running services:
echo.

REM List running VNO containers
set "found=0"
for /f "tokens=*" %%i in ('docker ps --filter "name=vno-" --format "{{.Names}}"') do (
    set "found=1"
    echo   - %%i
)

if "%found%"=="0" (
    echo   No VNO services are currently running.
    echo.
    pause
    goto main_menu
)

echo.
echo Available services to stop:
echo.
echo 1. Auth Service
echo 2. User Service
echo 3. Note Service
echo 4. Realtime Collab Service
echo 5. Notification Producer
echo 6. Notification Processor
echo 7. All Services
echo 8. Back to main menu
echo.
set /p "service=Choose a service to stop (1-8): "

if "%service%"=="1" (
    set "service_name=auth-service"
    goto do_stop
)
if "%service%"=="2" (
    set "service_name=user-service"
    goto do_stop
)
if "%service%"=="3" (
    set "service_name=note-service"
    goto do_stop
)
if "%service%"=="4" (
    set "service_name=realtime-collab-service"
    goto do_stop
)
if "%service%"=="5" (
    set "service_name=notification-producer"
    goto do_stop
)
if "%service%"=="6" (
    set "service_name=notification-processor"
    goto do_stop
)
if "%service%"=="7" goto stop_all
if "%service%"=="8" goto main_menu

echo Invalid choice. Please try again.
timeout /t 2 >nul
goto stop_service

:do_stop
echo.
echo Stopping %service_name%...

docker stop vno-%service_name%-container >nul 2>&1
if errorlevel 1 (
    echo [INFO] Container vno-%service_name%-container is not running
) else (
    echo [OK] Stopped vno-%service_name%-container
)

docker rm vno-%service_name%-container >nul 2>&1
if not errorlevel 1 (
    echo [OK] Removed vno-%service_name%-container
)

echo.
pause
goto main_menu

:stop_all
echo.
echo Stopping all services...
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
    
    docker rm vno-%%s-container >nul 2>&1
    if not errorlevel 1 (
        echo [OK] Removed vno-%%s-container
    )
)

echo.
echo All services stopped!
echo.
pause
goto main_menu

:view_containers
cls
echo.
echo ========================================
echo    Running Containers
echo ========================================
echo.

docker ps --filter "name=vno-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
pause
goto main_menu

:view_logs
cls
echo.
echo ========================================
echo    View Service Logs
echo ========================================
echo.
echo Running services:
echo.

REM List running VNO containers
set "found=0"
set "count=0"
for /f "tokens=*" %%i in ('docker ps --filter "name=vno-" --format "{{.Names}}"') do (
    set "found=1"
    set /a count+=1
    echo !count!. %%i
    set "container_!count!=%%i"
)

if "%found%"=="0" (
    echo   No VNO services are currently running.
    echo.
    pause
    goto main_menu
)

echo.
set /a count+=1
echo %count%. Back to main menu
echo.
set /p "choice=Choose a service to view logs (1-%count%): "

if "%choice%"=="%count%" goto main_menu

if defined container_%choice% (
    set "selected_container=!container_%choice%!"
    echo.
    echo Showing logs for !selected_container! (Press Ctrl+C to stop)...
    echo.
    timeout /t 2 >nul
    docker logs -f !selected_container!
    goto main_menu
) else (
    echo Invalid choice.
    timeout /t 2 >nul
    goto view_logs
)

:end
echo.
echo Goodbye!
endlocal
exit /b 0
