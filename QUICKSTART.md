# VNO Backend - Quick Reference

## 🚀 Quick Commands

### Build Everything
```cmd
REM Build all services
gradlew buildAll

REM Build all + create Docker images
build-and-deploy.bat

REM Build Docker images only (requires prior build)
build-all-images.bat
```

### Development
```cmd
REM Start all services in dev mode (separate windows)
dev-start.bat

REM Start single service in dev mode
cd auth-service && gradlew quarkusDev
```

### Testing
```cmd
REM Test all services
gradlew testAll

REM Test single service
gradlew :auth-service:test
```

### Cleaning
```cmd
REM Clean all
gradlew cleanAll

REM Clean single service
gradlew :auth-service:clean
```

## 🐳 Docker Commands

### Build Images
```cmd
REM All images
build-all-images.bat

REM Single image
cd auth-service
gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest .
```

### List Images
```cmd
docker images | findstr vno-
```

### Run Container
```cmd
REM Interactive menu (RECOMMENDED)
docker-services.bat

REM Or run individual service (port auto-mapped)
run-auth-service.bat
run-user-service.bat
run-note-service.bat
run-realtime-collab-service.bat
run-notification-producer.bat
run-notification-processor.bat

REM Run all services at once (detached mode)
run-all-services.bat

REM Stop all services
stop-all-services.bat

REM Or run manually
docker run -p 8080:8080 vno-auth-service:latest
```

### Remove Images
```cmd
REM Remove all VNO images
for /f "tokens=3" %i in ('docker images ^| findstr vno-') do docker rmi %i
```

## 🌐 Kong Gateway

### Start Kong (Local Development)
```cmd
cd gateway
docker-compose up -d
```

**Services auto-register!** No need to run registration scripts.

### Start Complete Stack (Docker)
```cmd
REM Build images first
build-and-deploy.bat

REM Start all services + Kong
cd gateway
docker-compose -f docker-compose-services.yml up -d
```

### Stop Kong
```cmd
cd gateway
docker-compose down

REM Or for complete stack
docker-compose -f docker-compose-services.yml down
```

### Verify Configuration
```cmd
REM Check registered services
curl http://localhost:8001/services

REM Check routes
curl http://localhost:8001/routes
```

## 📊 Service Endpoints

### Direct Access
- Auth: `http://localhost:8080`
- User: `http://localhost:8081`
- Note: `http://localhost:8082`
- Realtime: `http://localhost:8083`
- Notif Producer: `http://localhost:8084`
- Notif Processor: `http://localhost:8085`

### Through Kong Gateway
- Auth: `http://localhost:8000/api/auth`
- User: `http://localhost:8000/api/users`
- Note: `http://localhost:8000/api/notes`
- Realtime: `http://localhost:8000/api/collab`
- Notifications: `http://localhost:8000/api/notifications`

### Kong Admin
- Admin API: `http://localhost:8001`
- Admin GUI: `http://localhost:8002`

## 🔧 Gradle Tasks

### Root Project
| Task | Command |
|------|---------|
| Build all | `gradlew buildAll` |
| Clean all | `gradlew cleanAll` |
| Test all | `gradlew testAll` |
| Build Docker images | `gradlew buildAllDockerImages` |

### Individual Services
| Task | Command |
|------|---------|
| Build service | `gradlew :auth-service:build` |
| Test service | `gradlew :auth-service:test` |
| Clean service | `gradlew :auth-service:clean` |
| Dev mode | `gradlew :auth-service:quarkusDev` |

## 🐛 Troubleshooting

### Gradle Issues
```cmd
REM Refresh dependencies
gradlew cleanAll buildAll --refresh-dependencies

REM Clear Gradle cache
rmdir /s /q %USERPROFILE%\.gradle\caches
```

### Docker Issues
```cmd
REM Check Docker status
docker info

REM Restart Docker Desktop
REM (Use Docker Desktop UI)

REM Clean build cache
docker builder prune -a
```

### Port Conflicts
```cmd
REM Check port usage
netstat -ano | findstr :8080

REM Kill process on port (replace PID)
taskkill /PID <PID> /F
```

### Service Not Starting
```cmd
REM Check logs
gradlew :auth-service:quarkusDev --info

REM Check database connection
psql -U vno_auth_2025 -d vno_auths
```

## 📁 Project Structure

```
vno-backend/
├── auth-service/              # Port 8080
├── user-service/              # Port 8081
├── note-service/              # Port 8082
├── realtime-collab-service/   # Port 8083
├── notification-service/
│   ├── producer/              # Port 8084
│   └── processor/             # Port 8085
├── gateway/                   # Kong Gateway
├── build.gradle.kts           # Root build
├── settings.gradle.kts        # Multi-project settings
├── build-all-images.bat       # Build all Docker images
├── build-and-deploy.bat       # Build + Docker
└── dev-start.bat              # Start all in dev mode
```

## 📚 Documentation

- [BUILD.md](BUILD.md) - Comprehensive build guide
- [PORTS.md](PORTS.md) - Port configuration
- [gateway/README.md](gateway/README.md) - Kong Gateway setup

## 💡 Common Workflows

### Local Development
```cmd
REM 1. Start services
dev-start.bat

REM 2. Start Kong (services auto-register)
cd gateway && docker-compose up -d

REM 3. Test
curl http://localhost:8000/api/auth/health
```

### Docker Deployment
```cmd
REM 1. Build everything
build-and-deploy.bat

REM 2. Start complete stack (services auto-register)
cd gateway && docker-compose -f docker-compose-services.yml up -d

REM 3. Test
curl http://localhost:8000/api/auth/health
```

### Quick Rebuild
```cmd
REM Clean, build, dockerize
gradlew cleanAll && gradlew buildAll -x test && build-all-images.bat
```

## 📝 Notes

- All scripts are now in **Batch (.bat)** format for Windows compatibility
- Use `gradlew` (without `./`) in Windows Command Prompt
- Use `.\gradlew` in PowerShell
- Batch scripts can be run directly: `build-all-images.bat`
