# Build All Docker Images Script
# This script builds all VNO service Docker images
# Usage: .\build-all-images.ps1

$ErrorActionPreference = "Stop"


# Function to build a Docker image
function Build-DockerImage {
    param (
        [string]$ServiceName,
        [string]$ServicePath,
        [string]$ImageName,
        [string]$DockerfilePath = "src/main/docker/Dockerfile.jvm"
    )

    Write-Host "📦 Building $ServiceName..." -ForegroundColor Green
    
    Push-Location $ServicePath
    
    try {
        $output = docker build -f $DockerfilePath -t "${ImageName}:latest" . 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   ✓ $ServiceName image built successfully" -ForegroundColor Green
        } else {
            Write-Host "   ✗ Failed to build $ServiceName image" -ForegroundColor Red
            Write-Host $output -ForegroundColor Red
            Pop-Location
            exit 1
        }
    } catch {
        Write-Host "   ✗ Error building $ServiceName : $_" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    
    Pop-Location
    Write-Host ""
}

# Check if Docker is running
Write-Host "🔍 Checking Docker status..." -ForegroundColor Yellow
try {
    docker info | Out-Null
    Write-Host "✓ Docker is running" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "✗ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Build all service images
$startTime = Get-Date

Build-DockerImage -ServiceName "Auth Service" `
                  -ServicePath "auth-service" `
                  -ImageName "vno-auth-service"

Build-DockerImage -ServiceName "User Service" `
                  -ServicePath "user-service" `
                  -ImageName "vno-user-service"

Build-DockerImage -ServiceName "Note Service" `
                  -ServicePath "note-service" `
                  -ImageName "vno-note-service"

Build-DockerImage -ServiceName "Realtime Collab Service" `
                  -ServicePath "realtime-collab-service" `
                  -ImageName "vno-realtime-collab-service"

Build-DockerImage -ServiceName "Notification Producer" `
                  -ServicePath "notification-service" `
                  -ImageName "vno-notification-producer" `
                  -DockerfilePath "producer/src/main/docker/Dockerfile.jvm"

Build-DockerImage -ServiceName "Notification Processor" `
                  -ServicePath "notification-service" `
                  -ImageName "vno-notification-processor" `
                  -DockerfilePath "processor/src/main/docker/Dockerfile.jvm"

$endTime = Get-Date
$duration = $endTime - $startTime

Write-Host "✅ All Docker images built successfully!" -ForegroundColor Green
Write-Host "⏱️  Total time: $($duration.ToString('mm\:ss'))" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Built images:" -ForegroundColor Cyan
Write-Host "   - vno-auth-service:latest" -ForegroundColor White
Write-Host "   - vno-user-service:latest" -ForegroundColor White
Write-Host "   - vno-note-service:latest" -ForegroundColor White
Write-Host "   - vno-realtime-collab-service:latest" -ForegroundColor White
Write-Host "   - vno-notification-producer:latest" -ForegroundColor White
Write-Host "   - vno-notification-processor:latest" -ForegroundColor White
Write-Host ""
Write-Host "💡 Next steps:" -ForegroundColor Yellow
Write-Host "   1. Start Kong Gateway: cd gateway && docker-compose up -d" -ForegroundColor White
Write-Host "   2. Register services: cd gateway && .\register-services-docker.ps1" -ForegroundColor White
