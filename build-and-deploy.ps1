# Build and Deploy Script
# This script builds all services and creates Docker images in one command
# Usage: .\build-and-deploy.ps1

$ErrorActionPreference = "Stop"

Write-Host "🚀 VNO Backend - Build and Deploy All Services" -ForegroundColor Cyan
Write-Host ""

# Step 1: Clean all
Write-Host "🧹 Step 1/3: Cleaning all services..." -ForegroundColor Yellow
./gradlew cleanAll
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Clean failed" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Clean completed" -ForegroundColor Green
Write-Host ""

# Step 2: Build all
Write-Host "🔨 Step 2/3: Building all services..." -ForegroundColor Yellow
./gradlew buildAll -x test
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build completed" -ForegroundColor Green
Write-Host ""

# Step 3: Build Docker images
Write-Host "🐳 Step 3/3: Building Docker images..." -ForegroundColor Yellow
./build-all-images.ps1
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Docker build failed" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 All services built and dockerized successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Summary:" -ForegroundColor Cyan
Write-Host "   ✓ All services cleaned" -ForegroundColor White
Write-Host "   ✓ All services built" -ForegroundColor White
Write-Host "   ✓ All Docker images created" -ForegroundColor White
Write-Host ""
Write-Host "💡 Next steps:" -ForegroundColor Yellow
Write-Host "   1. Start Kong Gateway: cd gateway && docker-compose up -d" -ForegroundColor White
Write-Host "   2. Register services: cd gateway && .\register-services-docker.ps1" -ForegroundColor White
Write-Host "   3. Test services: curl http://localhost:8000/api/auth/health" -ForegroundColor White
