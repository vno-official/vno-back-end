# Kong Gateway - Declarative Configuration

This directory contains Kong Gateway configuration using **declarative YAML files** for automatic service registration.

## 🎯 Overview

Kong Gateway is configured in **DB-less mode** using declarative configuration files. Services are automatically registered when Kong starts - **no manual registration scripts needed!**

### Configuration Files

| File | Purpose |
|------|---------|
| `kong.yml` | Local development - services on host machine |
| `kong-docker.yml` | Docker deployment - services in containers |
| `docker-compose.yml` | Kong Gateway only (for local dev) |
| `docker-compose-services.yml` | Complete stack (all services + Kong) |

## 🚀 Quick Start

### Option 1: Local Development (Recommended)

Run services locally on your machine, Kong in Docker:

```cmd
REM 1. Start all services locally
dev-start.bat

REM 2. Start Kong Gateway
cd gateway
docker-compose up -d

REM 3. Services are auto-registered! Test immediately:
curl http://localhost:8000/api/auth/health
```

**That's it!** No registration scripts needed. Kong reads `kong.yml` and auto-registers all services.

### Option 2: Full Docker Deployment

Run everything in Docker containers:

```cmd
REM 1. Build all service images
build-and-deploy.bat

REM 2. Start entire stack (all services + Kong)
cd gateway
docker-compose -f docker-compose-services.yml up -d

REM 3. Services are auto-registered! Test immediately:
curl http://localhost:8000/api/auth/health
```

## 📋 Detailed Usage

### Local Development Setup

**Step 1: Start Kong Gateway**
```cmd
cd gateway
docker-compose up -d
```

This starts Kong in DB-less mode, automatically loading `kong.yml` which registers all services pointing to `host.docker.internal:808X`.

**Step 2: Start Your Services**
```cmd
REM Option A: Start all services at once
dev-start.bat

REM Option B: Start individual services
cd auth-service && gradlew quarkusDev
cd user-service && gradlew quarkusDev
REM ... etc
```

**Step 3: Access Through Kong**
```cmd
REM All services available immediately at:
curl http://localhost:8000/api/auth
curl http://localhost:8000/api/users
curl http://localhost:8000/api/notes
curl http://localhost:8000/api/collab
curl http://localhost:8000/api/notifications
```

### Docker Deployment Setup

**Step 1: Build Images**
```cmd
REM Build all services
gradlew buildAll

REM Create Docker images
build-all-images.bat
```

**Step 2: Start Complete Stack**
```cmd
cd gateway
docker-compose -f docker-compose-services.yml up -d
```

This starts:
- All 6 VNO services in containers
- Kong Gateway with `kong-docker.yml` configuration
- Shared Docker network for service discovery

**Step 3: Verify**
```cmd
REM Check all containers are running
docker-compose -f docker-compose-services.yml ps

REM Test services through Kong
curl http://localhost:8000/api/auth/health
```

## 🔧 Configuration Details

### kong.yml (Local Development)

Services point to `host.docker.internal` to access services on host machine:

```yaml
services:
  - name: auth-service
    url: http://host.docker.internal:8080
    routes:
      - name: auth-service-route
        paths:
          - /api/auth
```

### kong-docker.yml (Docker Deployment)

Services point to container names for Docker network communication:

```yaml
services:
  - name: auth-service
    url: http://vno-auth-service:8080
    routes:
      - name: auth-service-route
        paths:
          - /api/auth
```

### Adding a New Service

Edit the appropriate `kong.yml` or `kong-docker.yml`:

```yaml
services:
  - name: my-new-service
    url: http://host.docker.internal:8086  # or container name
    routes:
      - name: my-new-service-route
        paths:
          - /api/myservice
        strip_path: false
```

Then reload Kong:
```cmd
docker-compose restart kong
```

## 🌐 Access Points

### Service Endpoints (Through Kong)
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

### Direct Service Access (Bypass Kong)
- Auth Service: `http://localhost:8080`
- User Service: `http://localhost:8081`
- Note Service: `http://localhost:8082`
- Realtime Collab: `http://localhost:8083`
- Notification Producer: `http://localhost:8084`
- Notification Processor: `http://localhost:8085`

## 🐛 Troubleshooting

### Kong not starting
```cmd
REM Check Kong logs
docker-compose logs kong

REM Common issue: Invalid kong.yml syntax
REM Validate YAML syntax online or with yamllint
```

### Services not accessible through Kong
```cmd
REM 1. Verify Kong is running
docker-compose ps

REM 2. Check Kong loaded the config
curl http://localhost:8001/services

REM 3. Verify service is running
curl http://localhost:8080/q/health  # Direct access

REM 4. Check Kong can reach service
docker-compose logs kong | findstr "upstream"
```

### Reload Configuration After Changes
```cmd
REM Restart Kong to reload declarative config
docker-compose restart kong

REM Or for full stack
docker-compose -f docker-compose-services.yml restart kong
```

### Reset Everything
```cmd
REM Stop all containers
docker-compose down
REM or
docker-compose -f docker-compose-services.yml down

REM Start fresh
docker-compose up -d
```

## 📚 Legacy Scripts (Deprecated)

The following scripts are **no longer needed** with declarative configuration:
- ❌ `register-services-local.bat` - Services auto-register from `kong.yml`
- ❌ `register-services-docker.bat` - Services auto-register from `kong-docker.yml`
- ❌ `clear-services.bat` - Not needed in DB-less mode

These are kept for reference but should not be used.

## 💡 Benefits of Declarative Configuration

✅ **No Manual Registration** - Services auto-register on Kong startup  
✅ **Version Control** - Configuration is in Git, not in database  
✅ **Reproducible** - Same config produces same results every time  
✅ **Faster Startup** - No database migrations needed  
✅ **Easier CI/CD** - Just mount the YAML file  
✅ **Declarative** - Configuration as code, easier to review

## 📖 Additional Resources

- [Kong DB-less Mode Documentation](https://docs.konghq.com/gateway/latest/production/deployment-topologies/db-less-and-declarative-config/)
- [Declarative Configuration Format](https://docs.konghq.com/gateway/latest/production/deployment-topologies/db-less-and-declarative-config/#the-declarative-configuration-format)
- [Kong Gateway Docker](https://hub.docker.com/r/kong/kong-gateway)
