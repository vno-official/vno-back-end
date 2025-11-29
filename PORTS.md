# VNO Backend - Service Port Configuration

This document describes the port configuration for all VNO services.

## Port Mapping

| Service | Port | Container Name | Route Path |
|---------|------|----------------|------------|
| Auth Service | 8080 | vno-auth-service | /api/auth |
| User Service | 8081 | vno-user-service | /api/users |
| Note Service | 8082 | vno-note-service | /api/notes |
| Realtime Collab Service | 8083 | vno-realtime-collab-service | /api/collab |
| Notification Producer | 8084 | vno-notification-producer | /api/notifications |
| Notification Processor | 8085 | vno-notification-processor | /api/notifications/processor |

## Kong Gateway Ports

| Service | Port | Description |
|---------|------|-------------|
| Kong Proxy | 8000 | API Gateway endpoint |
| Kong Proxy SSL | 8443 | API Gateway SSL endpoint |
| Kong Admin API | 8001 | Admin API |
| Kong Admin API SSL | 8444 | Admin API SSL |
| Kong Admin GUI | 8002 | Admin GUI |
| Kong Admin GUI SSL | 8445 | Admin GUI SSL |

## Configuration Files

### Application Configuration (application.yml)
Each service has its port configured in `src/main/resources/application.yml`:

```yaml
quarkus:
  http:
    port: <PORT_NUMBER>
```

### Dockerfile Configuration
Each service's Dockerfile exposes the corresponding port:

```dockerfile
EXPOSE <PORT_NUMBER>
```

### Kong Registration Scripts
- **Local Development**: `gateway/register-services-local.ps1`
  - Uses `host.docker.internal` to access services on host machine
- **Docker Container**: `gateway/register-services-docker.ps1`
  - Uses container names for service discovery

## Building Docker Images

```powershell
# Auth Service (Port 8080)
cd auth-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest .

# User Service (Port 8081)
cd user-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-user-service:latest .

# Note Service (Port 8082)
cd note-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-note-service:latest .

# Realtime Collab Service (Port 8083)
cd realtime-collab-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-realtime-collab-service:latest .

# Notification Producer (Port 8084)
cd notification-service
./gradlew :producer:build
docker build -f producer/src/main/docker/Dockerfile.jvm -t vno-notification-producer:latest .

# Notification Processor (Port 8085)
cd notification-service
./gradlew :processor:build
docker build -f processor/src/main/docker/Dockerfile.jvm -t vno-notification-processor:latest .
```

## Running Services Locally

```powershell
# Each service in a separate terminal
cd auth-service && ./gradlew quarkusDev          # Port 8080
cd user-service && ./gradlew quarkusDev          # Port 8081
cd note-service && ./gradlew quarkusDev          # Port 8082
cd realtime-collab-service && ./gradlew quarkusDev  # Port 8083
cd notification-service/producer && ./gradlew quarkusDev  # Port 8084
cd notification-service/processor && ./gradlew quarkusDev # Port 8085
```

## Accessing Services

### Direct Access (without Kong)
- Auth Service: `http://localhost:8080`
- User Service: `http://localhost:8081`
- Note Service: `http://localhost:8082`
- Realtime Collab: `http://localhost:8083`
- Notification Producer: `http://localhost:8084`
- Notification Processor: `http://localhost:8085`

### Through Kong Gateway
After registering services with Kong:
- Auth Service: `http://localhost:8000/api/auth`
- User Service: `http://localhost:8000/api/users`
- Note Service: `http://localhost:8000/api/notes`
- Realtime Collab: `http://localhost:8000/api/collab`
- Notification Producer: `http://localhost:8000/api/notifications`
- Notification Processor: `http://localhost:8000/api/notifications/processor`

## Notes

- All services use **Quarkus** framework
- Default Quarkus port is 8080, but we override it in `application.yml`
- Docker containers expose the same port as configured in application
- Kong Gateway routes all traffic through port 8000
- For local development, Kong uses `host.docker.internal` to access services on the host machine
- For Docker deployment, Kong uses container names for service discovery
