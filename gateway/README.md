# Kong Gateway Scripts

This directory contains PowerShell scripts to manage Kong Gateway service registration.

## Prerequisites

1. Kong Gateway must be running (via docker-compose)
2. PowerShell 5.1 or higher

## Start Kong Gateway

```powershell
docker-compose up -d
```

Wait for Kong to be fully initialized (about 30 seconds).

## Scripts

### 1. Register Services - Local Development

Use this when your services are running locally (outside Docker):

```powershell
.\register-services-local.ps1
```

This script registers services using `host.docker.internal` to allow Kong (running in Docker) to access services on your host machine.

**Service Ports (Local):**
- auth-service: `localhost:8080`
- user-service: `localhost:8081`
- note-service: `localhost:8082`
- realtime-collab-service: `localhost:8083`
- notification-producer: `localhost:8084`
- notification-processor: `localhost:8085`

### 2. Register Services - Docker Container

Use this when all services are running in Docker containers:

```powershell
.\register-services-docker.ps1
```

This script registers services using container names as hostnames.

**Container Names:**
- `vno-auth-service`
- `vno-user-service`
- `vno-note-service`
- `vno-realtime-collab-service`
- `vno-notification-producer`
- `vno-notification-processor`

### 3. Clear All Services

Remove all registered services from Kong:

```powershell
.\clear-services.ps1
```

## Access Points

After registration, all services are accessible through Kong Gateway:

### API Gateway (Proxy)
- **Base URL**: `http://localhost:8000`
- Auth Service: `http://localhost:8000/api/auth`
- User Service: `http://localhost:8000/api/users`
- Note Service: `http://localhost:8000/api/notes`
- Realtime Collab: `http://localhost:8000/api/collab`
- Notifications: `http://localhost:8000/api/notifications`
- Notifications Processor: `http://localhost:8000/api/notifications/processor`

### Kong Admin
- **Admin API**: `http://localhost:8001`
- **Admin GUI**: `http://localhost:8002`

## Example Usage

### Local Development Workflow

1. Start Kong Gateway:
```powershell
cd gateway
docker-compose up -d
```

2. Start your services locally:
```powershell
# Terminal 1 - Auth Service
cd auth-service
./gradlew quarkusDev

# Terminal 2 - User Service
cd user-service
./gradlew quarkusDev

# ... and so on
```

3. Register services to Kong:
```powershell
cd gateway
.\register-services-local.ps1
```

4. Test through Kong Gateway:
```powershell
curl http://localhost:8000/api/auth/health
curl http://localhost:8000/api/users/health
```

### Docker Container Workflow

1. Build all service images:
```powershell
# Auth Service
cd auth-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest .

# Repeat for other services...
```

2. Start all containers (you'll need a docker-compose.yml for all services)

3. Register services to Kong:
```powershell
cd gateway
.\register-services-docker.ps1
```

## Troubleshooting

### Kong not responding
```powershell
# Check Kong status
docker-compose ps

# View Kong logs
docker-compose logs kong

# Restart Kong
docker-compose restart kong
```

### Service registration fails
```powershell
# Verify Kong Admin API is accessible
curl http://localhost:8001/status

# Check if service is running
curl http://localhost:8080/q/health  # for local services
```

### Clear and re-register
```powershell
.\clear-services.ps1
.\register-services-local.ps1  # or register-services-docker.ps1
```

## Notes

- The scripts use `PUT` method for idempotent registration (can run multiple times)
- Routes are configured with `strip_path = false` to preserve the full path
- All scripts include retry logic and error handling
- Service URLs can be customized by editing the respective script
