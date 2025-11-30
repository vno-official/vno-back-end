@echo off
REM Run Notification Processor Container
REM Port 8085 is automatically mapped

echo Starting Notification Processor container...
docker run --rm -p 8085:8085 --name vno-notification-processor-container vno-notification-processor:latest
