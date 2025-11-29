# Kong Gateway - Clear All Services Script
# This script removes all registered services and routes from Kong Gateway
# Usage: .\clear-services.ps1

$KONG_ADMIN_URL = "http://localhost:8001"

Write-Host "🗑️  Clearing all services from Kong Gateway" -ForegroundColor Cyan
Write-Host "Kong Admin URL: $KONG_ADMIN_URL" -ForegroundColor Yellow
Write-Host ""

# List of service names to remove
$services = @(
    "auth-service",
    "user-service",
    "note-service",
    "realtime-collab-service",
    "notification-producer",
    "notification-processor"
)

# Function to delete a service
function Remove-Service {
    param (
        [string]$ServiceName
    )

    Write-Host "🗑️  Removing service: $ServiceName" -ForegroundColor Yellow

    try {
        $response = Invoke-RestMethod -Uri "$KONG_ADMIN_URL/services/$ServiceName" -Method Delete -ErrorAction Stop
        Write-Host "   ✓ Service '$ServiceName' removed successfully" -ForegroundColor Green
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) {
            Write-Host "   ℹ️  Service '$ServiceName' not found (already removed)" -ForegroundColor Gray
        } else {
            Write-Host "   ✗ Failed to remove service '$ServiceName': $_" -ForegroundColor Red
        }
    }
}

# Check if Kong is ready
Write-Host "⏳ Checking Kong Gateway status..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$KONG_ADMIN_URL/status" -Method Get -ErrorAction Stop
    Write-Host "✓ Kong Gateway is ready!" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "✗ Kong Gateway is not responding. Please check if Kong is running." -ForegroundColor Red
    exit 1
}

# Remove all services
foreach ($service in $services) {
    Remove-Service -ServiceName $service
}

Write-Host ""
Write-Host "✅ All services cleared successfully!" -ForegroundColor Green
