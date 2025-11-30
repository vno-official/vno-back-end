@echo off
REM Run Note Service Container
REM Port 8082 is automatically mapped

echo Starting Note Service container...
docker run --rm -p 8082:8082 --name vno-note-service-container vno-note-service:latest
