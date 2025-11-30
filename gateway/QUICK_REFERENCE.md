# Kong Gateway - Quick Reference

## 🚀 Quick Commands

### Local Development
```cmd
REM Start services locally
dev-start.bat

REM Start Kong (auto-registers services)
cd gateway
docker-compose up -d

REM Test
curl http://localhost:8000/api/auth/health
```

### Full Docker Deployment
```cmd
REM Build everything
build-and-deploy.bat

REM Start complete stack (all services + Kong)
cd gateway
docker-compose -f docker-compose-services.yml up -d

REM Test
curl http://localhost:8000/api/auth/health
```

## 📁 Configuration Files

| File | Purpose |
|------|---------|
| `kong.yml` | Local dev - services on `host.docker.internal` |
| `kong-docker.yml` | Docker - services use container names |
| `docker-compose.yml` | Kong only (for local dev) |
| `docker-compose-services.yml` | Complete stack (all services + Kong) |

## 🌐 Endpoints

### Through Kong Gateway
- Base: `http://localhost:8000`
- Auth: `http://localhost:8000/api/auth`
- User: `http://localhost:8000/api/users`
- Note: `http://localhost:8000/api/notes`
- Collab: `http://localhost:8000/api/collab`
- Notifications: `http://localhost:8000/api/notifications`

### Kong Admin
- API: `http://localhost:8001`
- GUI: `http://localhost:8002`

### Direct Access (Bypass Kong)
- Auth: `http://localhost:8080`
- User: `http://localhost:8081`
- Note: `http://localhost:8082`
- Collab: `http://localhost:8083`
- Notif Producer: `http://localhost:8084`
- Notif Processor: `http://localhost:8085`

## 🔧 Common Tasks

### Verify Kong Configuration
```cmd
REM List all services
curl http://localhost:8001/services

REM List all routes
curl http://localhost:8001/routes
```

### Reload Configuration
```cmd
REM After editing kong.yml or kong-docker.yml
docker-compose restart kong
```

### View Logs
```cmd
REM Kong logs
docker-compose logs -f kong

REM All services logs
docker-compose -f docker-compose-services.yml logs -f
```

### Stop Everything
```cmd
REM Stop Kong only
docker-compose down

REM Stop complete stack
docker-compose -f docker-compose-services.yml down
```

## ✨ Key Features

✅ **Auto-Registration** - No manual scripts needed  
✅ **DB-less Mode** - No PostgreSQL database required  
✅ **Version Control** - Configuration in Git  
✅ **Fast Startup** - No database migrations  
✅ **Easy Updates** - Edit YAML and restart

## 🆕 Adding a Service

1. Edit `kong.yml` (or `kong-docker.yml`):
```yaml
services:
  - name: new-service
    url: http://host.docker.internal:8086
    routes:
      - name: new-service-route
        paths:
          - /api/newservice
        strip_path: false
```

2. Restart Kong:
```cmd
docker-compose restart kong
```

3. Test:
```cmd
curl http://localhost:8000/api/newservice/health
```

## 🐛 Troubleshooting

### Kong not starting
```cmd
docker-compose logs kong
REM Check for YAML syntax errors
```

### Service not accessible
```cmd
REM 1. Check service is running
curl http://localhost:8080/q/health

REM 2. Check Kong registered it
curl http://localhost:8001/services

REM 3. Check Kong can reach it
docker-compose logs kong | findstr "upstream"
```

### Reset everything
```cmd
docker-compose down
docker-compose up -d
```
