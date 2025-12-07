# Development Setup (Neon PostgreSQL)

## Quick Start with External Neon Database

Since you're using **Neon PostgreSQL**, you don't need Docker for development!

### 1. Configure Database Connection

Create `.env` file (copy from `.env.development`):

```bash
cp .env.development .env
```

Edit `.env` with your Neon database credentials:

```properties
DATABASE_URL=jdbc:postgresql://your-project.neon.tech:5432/neondb?sslmode=require
DATABASE_REACTIVE_URL=postgresql://your-project.neon.tech:5432/neondb?sslmode=require
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
```

### 2. Install Redis (Optional)

**Option A: Use Upstash Redis (Cloud - Free tier)**
- Sign up at https://upstash.com
- Create Redis database
- Copy connection string to `.env`:
  ```
  REDIS_HOSTS=redis://default:password@host:port
  ```

**Option B: Install Redis locally (Windows)**
```powershell
# Using Chocolatey
choco install redis-64

# Or use WSL2
wsl -d Ubuntu
sudo apt update && sudo apt install redis-server
redis-server
```

**Option C: Skip Redis (for testing)**
- Comment out Redis configuration in `application.yml`
- Backend will run without caching

### 3. Run Development Server

```bash
./gradlew quarkusDev
```

That's it! Your backend will:
- ✅ Connect to Neon PostgreSQL
- ✅ Run migrations automatically
- ✅ Start on http://localhost:8080
- ✅ Auto-reload on code changes

### 4. Access Application

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Health**: http://localhost:8080/q/health
- **Dev UI**: http://localhost:8080/q/dev

---

## Docker Setup (For Production Only)

The Docker setup is mainly for **production deployment**, not local development.

See [DOCKER.md](DOCKER.md) for production deployment instructions.

---

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | JDBC connection string | `jdbc:postgresql://...` |
| `DATABASE_REACTIVE_URL` | Reactive connection string | `postgresql://...` |
| `DATABASE_USERNAME` | Database user | `neondb_owner` |
| `DATABASE_PASSWORD` | Database password | `your-password` |
| `REDIS_HOSTS` | Redis connection | `redis://localhost:6379` |
| `RESEND_API_KEY` | Email service API key | `re_...` |
| `APP_DOMAIN` | Application domain | `localhost` |

---

## Neon Database Tips

### Get Connection String
1. Go to https://console.neon.tech
2. Select your project
3. Click "Connection Details"
4. Copy connection string

### Connection String Format
```
postgresql://username:password@host/database?sslmode=require
```

### For JDBC (add `jdbc:` prefix):
```
jdbc:postgresql://host/database?sslmode=require
```

### Enable Pooling (Recommended)
Use Neon's pooled connection for better performance:
```
DATABASE_REACTIVE_URL=postgresql://username:password@host/database?sslmode=require&pooled=true
```

---

## Troubleshooting

### "Connection refused" error
- Check your Neon database is active (not paused)
- Verify connection string is correct
- Check if IP is whitelisted in Neon (if IP restrictions enabled)

### "SSL connection failed"
- Make sure `sslmode=require` is in connection string
- Neon requires SSL connections

### Migrations fail
- Check database user has CREATE TABLE permissions
- Verify Flyway is enabled in `application.yml`

### Redis connection error (if using)
- Make sure Redis is running: `redis-cli ping` should return `PONG`
- Check Redis connection string in `.env`

---

## Development Workflow

```bash
# 1. Start backend
./gradlew quarkusDev

# 2. Make changes to code
# (auto-reload happens automatically)

# 3. Test API
curl http://localhost:8080/api/auth/login

# 4. View Swagger
open http://localhost:8080/q/swagger-ui
```

---

## Next Steps

1. ✅ Configure `.env` with Neon credentials
2. ✅ Run `./gradlew quarkusDev`
3. ✅ Test endpoints in Swagger UI
4. ✅ Start coding!

For production deployment with Docker, see [DOCKER.md](DOCKER.md).
