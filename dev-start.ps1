# Quick Start Script
# This script starts all services in development mode
# Usage: .\dev-start.ps1

Write-Host "🚀 Starting VNO Backend Services in Development Mode" -ForegroundColor Cyan
Write-Host ""
Write-Host "This will start all services using Gradle's quarkusDev mode" -ForegroundColor Yellow
Write-Host "Each service will run in a separate PowerShell window" -ForegroundColor Yellow
Write-Host ""

# Function to start a service in a new window
function Start-ServiceDev {
    param (
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port
    )
    
    Write-Host "Starting $ServiceName on port $Port..." -ForegroundColor Green
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$ServicePath'; Write-Host '🚀 $ServiceName - Port $Port' -ForegroundColor Cyan; ./gradlew quarkusDev"
}

# Start all services
Start-ServiceDev -ServiceName "Auth Service" -ServicePath "$PSScriptRoot\auth-service" -Port 8080
Start-Sleep -Seconds 2

Start-ServiceDev -ServiceName "User Service" -ServicePath "$PSScriptRoot\user-service" -Port 8081
Start-Sleep -Seconds 2

Start-ServiceDev -ServiceName "Note Service" -ServicePath "$PSScriptRoot\note-service" -Port 8082
Start-Sleep -Seconds 2

Start-ServiceDev -ServiceName "Realtime Collab Service" -ServicePath "$PSScriptRoot\realtime-collab-service" -Port 8083
Start-Sleep -Seconds 2

Start-ServiceDev -ServiceName "Notification Producer" -ServicePath "$PSScriptRoot\notification-service\producer" -Port 8084
Start-Sleep -Seconds 2

Start-ServiceDev -ServiceName "Notification Processor" -ServicePath "$PSScriptRoot\notification-service\processor" -Port 8085

Write-Host ""
Write-Host "✅ All services are starting in separate windows" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Service endpoints:" -ForegroundColor Cyan
Write-Host "   - Auth Service:         http://localhost:8080" -ForegroundColor White
Write-Host "   - User Service:         http://localhost:8081" -ForegroundColor White
Write-Host "   - Note Service:         http://localhost:8082" -ForegroundColor White
Write-Host "   - Realtime Collab:      http://localhost:8083" -ForegroundColor White
Write-Host "   - Notification Producer: http://localhost:8084" -ForegroundColor White
Write-Host "   - Notification Processor: http://localhost:8085" -ForegroundColor White
Write-Host ""
Write-Host "💡 To use Kong Gateway:" -ForegroundColor Yellow
Write-Host "   1. Start Kong: cd gateway && docker-compose up -d" -ForegroundColor White
Write-Host "   2. Register services: cd gateway && .\register-services-local.ps1" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Press Ctrl+C in each window to stop the services" -ForegroundColor Yellow
