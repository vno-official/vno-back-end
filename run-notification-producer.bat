@echo off
REM Run Notification Producer Container
REM Port 8084 is automatically mapped

echo Starting Notification Producer container...
docker run --rm -p 8084:8084 --name vno-notification-producer-container vno-notification-producer:latest
