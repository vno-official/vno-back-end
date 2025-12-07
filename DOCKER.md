# Docker Deployment Guide

## Quick Start (Development)

### 1. Start all services

```bash
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
- Redis on port 6379
- VNO Backend on port 8080
- Adminer (database UI) on port 8081

### 2. Check service health

```bash
docker-compose ps
docker-compose logs -f backend
```

### 3. Access the application

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health
- Database UI: http://localhost:8081

### 4. Stop services

```bash
docker-compose down
```

### 5. Clean everything (including volumes)

```bash
docker-compose down -v
```

---

## Production Deployment

### Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- Domain with DNS configured
- SSL certificates (Let's Encrypt recommended)

### 1. Prepare environment

```bash
# Copy and configure environment file
cp .env.example .env.prod

# Edit with your production values
nano .env.prod
```

**Important**: Update these values in `.env.prod`:
- `POSTGRES_PASSWORD` - Strong database password
- `REDIS_PASSWORD` - Strong Redis password
- `RESEND_API_KEY` - Your Resend API key
- `APP_DOMAIN` - Your production domain
- `GOOGLE_CLIENT_ID` & `GOOGLE_CLIENT_SECRET` - OAuth credentials

### 2. Generate JWT keys

```bash
# Create keys directory
mkdir -p keys

# Generate private key
openssl genrsa -out keys/jwt-private.pem 2048

# Generate public key
openssl rsa -in keys/jwt-private.pem -pubout -out keys/jwt-public.pem

# Secure the keys
chmod 600 keys/jwt-private.pem
chmod 644 keys/jwt-public.pem
```

### 3. Setup SSL certificates

Using Let's Encrypt with Certbot:

```bash
# Create SSL directory
mkdir -p nginx/ssl

# Generate certificates (replace with your domain)
docker run -it --rm \
  -v $(pwd)/nginx/ssl:/etc/letsencrypt \
  certbot/certbot certonly \
  --standalone \
  -d api.yourdomain.com \
  --email your-email@example.com \
  --agree-tos

# Copy certificates to nginx directory
cp nginx/ssl/live/api.yourdomain.com/fullchain.pem nginx/ssl/
cp nginx/ssl/live/api.yourdomain.com/privkey.pem nginx/ssl/
```

### 4. Build and deploy

```bash
# Build images
docker-compose -f docker-compose.prod.yml build

# Start services
docker-compose -f docker-compose.prod.yml up -d

# Check logs
docker-compose -f docker-compose.prod.yml logs -f
```

### 5. Verify deployment

```bash
# Check all services are running
docker-compose -f docker-compose.prod.yml ps

# Test health endpoint
curl https://api.yourdomain.com/q/health

# Test API endpoint
curl https://api.yourdomain.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

---

## Docker Commands Reference

### Build

```bash
# Build single service
docker-compose build backend

# Build with no cache
docker-compose build --no-cache backend

# Build production image
docker-compose -f docker-compose.prod.yml build
```

### Run

```bash
# Start in foreground
docker-compose up

# Start in background (detached)
docker-compose up -d

# Start specific service
docker-compose up -d postgres redis

# Scale backend instances
docker-compose -f docker-compose.prod.yml up -d --scale backend=3
```

### Logs

```bash
# View all logs
docker-compose logs

# Follow logs
docker-compose logs -f

# Logs for specific service
docker-compose logs -f backend

# Last 100 lines
docker-compose logs --tail=100 backend
```

### Execute Commands

```bash
# Enter backend container
docker-compose exec backend sh

# Run database migrations
docker-compose exec backend java -jar quarkus-run.jar flyway migrate

# Access PostgreSQL
docker-compose exec postgres psql -U postgres -d vno
```

### Cleanup

```bash
# Stop services
docker-compose stop

# Stop and remove containers
docker-compose down

# Remove volumes too
docker-compose down -v

# Remove images
docker-compose down --rmi all

# Clean everything
docker system prune -a --volumes
```

---

## Database Management

### Backup

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres vno > backup-$(date +%Y%m%d).sql

# Or using docker run
docker run --rm \
  --network vno-backend_vno-network \
  postgres:16-alpine \
  pg_dump -h postgres -U postgres vno > backup.sql
```

### Restore

```bash
# Restore from backup
docker-compose exec -T postgres psql -U postgres vno < backup.sql

# Or using docker run
docker run --rm -i \
  --network vno-backend_vno-network \
  postgres:16-alpine \
  psql -h postgres -U postgres vno < backup.sql
```

### Automated Backups

Add this to your crontab:

```bash
# Daily backup at 2 AM
0 2 * * * cd /path/to/vno-backend && docker-compose exec postgres pg_dump -U postgres vno | gzip > backups/backup-$(date +\%Y\%m\%d).sql.gz
```

---

## Monitoring

### Health Checks

```bash
# Application health
curl http://localhost:8080/q/health

# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready
```

### Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/q/metrics
```

### Container Stats

```bash
# Real-time stats
docker stats

# Specific container
docker stats vno-backend
```

---

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker-compose logs backend

# Common issues:
# 1. Database not ready
docker-compose logs postgres

# 2. Port already in use
lsof -i :8080
kill -9 <PID>

# 3. Volume permissions
docker-compose down -v
docker-compose up -d
```

### Database connection error

```bash
# Verify database is running
docker-compose exec postgres pg_isready

# Check connection from backend
docker-compose exec backend curl postgres:5432

# Reset database
docker-compose down -v
docker-compose up -d postgres
# Wait 30 seconds
docker-compose up -d backend
```

### Out of memory

```bash
# Check memory usage
docker stats

# Increase Docker memory (Docker Desktop)
# Settings → Resources → Memory → Increase to 4GB+

# Or add to docker-compose.yml:
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 2G
```

---

## CI/CD Integration

### GitHub Actions

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: docker build -t vno-backend .
      
      - name: Push to registry
        run: |
          echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker tag vno-backend ghcr.io/${{ github.repository }}:latest
          docker push ghcr.io/${{ github.repository }}:latest
      
      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /opt/vno-backend
            docker-compose -f docker-compose.prod.yml pull
            docker-compose -f docker-compose.prod.yml up -d
```

---

## Security Best Practices

1. **Never commit `.env` files**
   ```bash
   # Add to .gitignore
   .env
   .env.prod
   .env.local
   ```

2. **Use strong passwords**
   ```bash
   # Generate random password
   openssl rand -base64 32
   ```

3. **Keep images updated**
   ```bash
   docker-compose pull
   docker-compose up -d
   ```

4. **Use secrets management** (Docker Swarm/Kubernetes)
   ```bash
   echo "my_password" | docker secret create db_password -
   ```

5. **Limit container resources**
   - Set memory limits
   - Set CPU limits
   - Use read-only file systems where possible

---

## Production Checklist

- [ ] Environment variables configured in `.env.prod`
- [ ] Strong passwords for database and Redis
- [ ] SSL certificates installed and configured
- [ ] JWT keys generated and secured
- [ ] Database backups automated
- [ ] Monitoring and logging configured
- [ ] Health checks working
- [ ] Resource limits set
- [ ] Security headers configured in Nginx
- [ ] Rate limiting enabled
- [ ] Firewall rules configured
- [ ] Domain DNS configured
- [ ] Email service configured and tested

---

## Support

For issues and questions:
- GitHub Issues: https://github.com/your-org/vno-backend/issues
- Email: support@yourdomain.com
