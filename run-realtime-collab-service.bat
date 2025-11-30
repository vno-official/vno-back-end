@echo off
REM Run Realtime Collab Service Container
REM Port 8083 is automatically mapped

echo Starting Realtime Collab Service container...
docker run --rm -p 8083:8083 --name vno-realtime-collab-service-container vno-realtime-collab-service:latest
