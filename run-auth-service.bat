@echo off
REM Run Auth Service Container
REM Port 8080 is automatically mapped

echo Starting Auth Service container...
docker run --rm -p 8080:8080 --name vno-auth-service-container vno-auth-service:latest
