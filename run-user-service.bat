@echo off
REM Run User Service Container
REM Port 8081 is automatically mapped

echo Starting User Service container...
docker run --rm -p 8081:8081 --name vno-user-service-container vno-user-service:latest
