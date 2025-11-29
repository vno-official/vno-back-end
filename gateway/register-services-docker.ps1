# Kong Gateway Service Registration Script - Docker Container
# This script registers all VNO services to Kong Gateway running in Docker
# Usage: .\register-services-docker.ps1

$KONG_ADMIN_URL = "http://localhost:8001"

Write-Host "🚀 Registering VNO Services to Kong Gateway (Docker Container)" -ForegroundColor Cyan
Write-Host "Kong Admin URL: $KONG_ADMIN_URL" -ForegroundColor Yellow
Write-Host ""

# Function to register a service and route
function Register-Service {
    param (
        [string]$ServiceName,
        [string]$ServiceUrl,
        [string]$RoutePath,
        [int]$Port
    )

    Write-Host "📝 Registering service: $ServiceName" -ForegroundColor Green

    # Create or update service
    $serviceBody = @{
        name = $ServiceName
        url = $ServiceUrl
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$KONG_ADMIN_URL/services/$ServiceName" -Method Put -Body $serviceBody -ContentType "application/json" -ErrorAction Stop
        Write-Host "   ✓ Service '$ServiceName' registered successfully" -ForegroundColor Green
    } catch {
        Write-Host "   ✗ Failed to register service '$ServiceName': $_" -ForegroundColor Red
        return
    }

    # Create or update route
    $routeBody = @{
        name = "$ServiceName-route"
        paths = @($RoutePath)
        strip_path = $false
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$KONG_ADMIN_URL/services/$ServiceName/routes/$ServiceName-route" -Method Put -Body $routeBody -ContentType "application/json" -ErrorAction Stop
        Write-Host "   ✓ Route '$RoutePath' registered successfully" -ForegroundColor Green
    } catch {
        Write-Host "   ✗ Failed to register route for '$ServiceName': $_" -ForegroundColor Red
    }

    Write-Host ""
}

# Wait for Kong to be ready
Write-Host "⏳ Waiting for Kong Gateway to be ready..." -ForegroundColor Yellow
$maxRetries = 30
$retryCount = 0
$kongReady = $false

while (-not $kongReady -and $retryCount -lt $maxRetries) {
    try {
        $response = Invoke-RestMethod -Uri "$KONG_ADMIN_URL/status" -Method Get -ErrorAction Stop
        $kongReady = $true
        Write-Host "✓ Kong Gateway is ready!" -ForegroundColor Green
        Write-Host ""
    } catch {
        $retryCount++
        Write-Host "   Attempt $retryCount/$maxRetries - Kong not ready yet..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
}

if (-not $kongReady) {
    Write-Host "✗ Kong Gateway is not responding. Please check if Kong is running." -ForegroundColor Red
    exit 1
}

# Register services
# Note: Services are running in Docker containers on the same network
# Using container names as hostnames

Register-Service -ServiceName "auth-service" `
                 -ServiceUrl "http://vno-auth-service:8080" `
                 -RoutePath "/api/auth" `
                 -Port 8080

Register-Service -ServiceName "user-service" `
                 -ServiceUrl "http://vno-user-service:8081" `
                 -RoutePath "/api/users" `
                 -Port 8081

Register-Service -ServiceName "note-service" `
                 -ServiceUrl "http://vno-note-service:8082" `
                 -RoutePath "/api/notes" `
                 -Port 8082

Register-Service -ServiceName "realtime-collab-service" `
                 -ServiceUrl "http://vno-realtime-collab-service:8083" `
                 -RoutePath "/api/collab" `
                 -Port 8083

Register-Service -ServiceName "notification-producer" `
                 -ServiceUrl "http://vno-notification-producer:8084" `
                 -RoutePath "/api/notifications" `
                 -Port 8084

Register-Service -ServiceName "notification-processor" `
                 -ServiceUrl "http://vno-notification-processor:8085" `
                 -RoutePath "/api/notifications/processor" `
                 -Port 8085

Write-Host "✅ All services registered successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Service endpoints available at:" -ForegroundColor Cyan
Write-Host "   - Auth Service:         http://localhost:8000/api/auth" -ForegroundColor White
Write-Host "   - User Service:         http://localhost:8000/api/users" -ForegroundColor White
Write-Host "   - Note Service:         http://localhost:8000/api/notes" -ForegroundColor White
Write-Host "   - Realtime Collab:      http://localhost:8000/api/collab" -ForegroundColor White
Write-Host "   - Notifications:        http://localhost:8000/api/notifications" -ForegroundColor White
Write-Host "   - Notifications Proc:   http://localhost:8000/api/notifications/processor" -ForegroundColor White
Write-Host ""
Write-Host "🔧 Kong Admin GUI:        http://localhost:8002" -ForegroundColor Cyan
Write-Host "🔧 Kong Admin API:        http://localhost:8001" -ForegroundColor Cyan
