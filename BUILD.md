# VNO Backend - Multi-Project Build

This is the root project for VNO Backend, containing all microservices.

## Project Structure

```
vno-backend/
├── auth-service/              # Authentication & Authorization Service (Port 8080)
├── user-service/              # User Management Service (Port 8081)
├── note-service/              # Note Management Service (Port 8082)
├── realtime-collab-service/   # Real-time Collaboration Service (Port 8083)
├── notification-service/      # Notification Service
│   ├── producer/              # Notification Producer (Port 8084)
│   └── processor/             # Notification Processor (Port 8085)
├── common-observability/      # Common observability utilities
├── common-openapi/            # Common OpenAPI definitions
├── gateway/                   # Kong API Gateway configuration
├── build.gradle.kts           # Root build configuration
├── settings.gradle.kts        # Multi-project settings
└── gradle.properties          # Gradle properties
```

## Prerequisites

- **JDK 21** or higher
- **Gradle 8.6** or higher (wrapper included)
- **Docker Desktop** (for containerization)
- **PostgreSQL** (for databases)
- **PowerShell** (for scripts)

## Quick Start

### 1. Build All Services

Build all services at once:

```powershell
./gradlew buildAll
```

Or build with tests:

```powershell
./gradlew buildAll test
```

### 2. Build Docker Images

Build all Docker images:

```powershell
./gradlew buildAllDockerImages
```

Or use the standalone script:

```powershell
./build-all-images.ps1
```

### 3. Build and Deploy (One Command)

Clean, build, and create Docker images:

```powershell
./build-and-deploy.ps1
```

This script will:
1. Clean all services
2. Build all services (skip tests)
3. Create all Docker images

## Development

### Start All Services in Dev Mode

Start all services in separate windows:

```powershell
./dev-start.ps1
```

Each service will run in Quarkus dev mode with hot reload.

### Start Individual Service

```powershell
cd auth-service
./gradlew quarkusDev
```

## Gradle Tasks

### Root Project Tasks

| Task | Description |
|------|-------------|
| `./gradlew buildAll` | Build all services |
| `./gradlew cleanAll` | Clean all services |
| `./gradlew testAll` | Run tests for all services |
| `./gradlew buildAllDockerImages` | Build all Docker images |

### Individual Service Tasks

```powershell
# Build specific service
./gradlew :auth-service:build
./gradlew :user-service:build
./gradlew :note-service:build
./gradlew :realtime-collab-service:build
./gradlew :notification-service:producer:build
./gradlew :notification-service:processor:build

# Run specific service in dev mode
./gradlew :auth-service:quarkusDev
```

## Docker Images

### Build Individual Image

```powershell
# Auth Service
cd auth-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest .

# User Service
cd user-service
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-user-service:latest .

# ... and so on
```

### List Built Images

```powershell
docker images | findstr vno-
```

### Run Container

```powershell
docker run -p 8080:8080 vno-auth-service:latest
```

## Kong API Gateway

### Start Kong Gateway

```powershell
cd gateway
docker-compose up -d
```

### Register Services

For local development (services running on host):

```powershell
cd gateway
./register-services-local.ps1
```

For Docker containers:

```powershell
cd gateway
./register-services-docker.ps1
```

### Access Services Through Kong

All services are accessible through Kong Gateway at `http://localhost:8000`:

- Auth Service: `http://localhost:8000/api/auth`
- User Service: `http://localhost:8000/api/users`
- Note Service: `http://localhost:8000/api/notes`
- Realtime Collab: `http://localhost:8000/api/collab`
- Notifications: `http://localhost:8000/api/notifications`

### Kong Admin

- Admin API: `http://localhost:8001`
- Admin GUI: `http://localhost:8002`

## Port Configuration

See [PORTS.md](PORTS.md) for detailed port configuration.

## Testing

### Run All Tests

```powershell
./gradlew testAll
```

### Run Tests for Specific Service

```powershell
./gradlew :auth-service:test
```

## Cleaning

### Clean All

```powershell
./gradlew cleanAll
```

### Clean Specific Service

```powershell
./gradlew :auth-service:clean
```

## Troubleshooting

### Gradle Build Issues

```powershell
# Clean and rebuild
./gradlew cleanAll buildAll --refresh-dependencies
```

### Docker Build Issues

```powershell
# Check Docker is running
docker info

# Clean Docker build cache
docker builder prune -a
```

### Port Conflicts

Check if ports are already in use:

```powershell
netstat -ano | findstr :8080
netstat -ano | findstr :8081
# ... etc
```

## Scripts

| Script | Description |
|--------|-------------|
| `build-all-images.ps1` | Build all Docker images |
| `build-and-deploy.ps1` | Clean, build, and dockerize all services |
| `dev-start.ps1` | Start all services in dev mode |
| `gateway/register-services-local.ps1` | Register services to Kong (local) |
| `gateway/register-services-docker.ps1` | Register services to Kong (Docker) |
| `gateway/clear-services.ps1` | Clear all services from Kong |

## Contributing

1. Create a feature branch
2. Make your changes
3. Run tests: `./gradlew testAll`
4. Build all: `./gradlew buildAll`
5. Submit a pull request

## License

[Your License Here]
