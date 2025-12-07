# Fly.io Deployment Guide

## JWT Key Configuration for Fly.io

Fly.io doesn't support mounting files easily, so we'll use **environment variables** for JWT keys.

### 1. Generate JWT Keys Locally

```bash
# Generate private key (RSA 2048-bit)
openssl genrsa -out privateKey.pem 2048

# Generate public key from private key
openssl rsa -in privateKey.pem -pubout -out publicKey.pem
```

### 2. Convert Private Key to Single-Line Format

```bash
# Windows PowerShell
$key = Get-Content privateKey.pem -Raw
$key = $key -replace "`r`n", "\n"
$key

# Linux/Mac
cat privateKey.pem | tr '\n' '|' | sed 's/|/\\n/g'
```

Copy the output (single-line string with `\n` for newlines).

### 3. Set Fly.io Secrets

```bash
# Set private key as secret
fly secrets set SMALLRYE_JWT_SIGN_KEY="-----BEGIN PRIVATE KEY-----\nMIIEvgI...\n-----END PRIVATE KEY-----"

# Set database URL
fly secrets set DATABASE_URL="jdbc:postgresql://your-neon-url:5432/db?sslmode=require"
fly secrets set DATABASE_REACTIVE_URL="postgresql://your-neon-url:5432/db?sslmode=require"
fly secrets set DATABASE_USERNAME="your-username"
fly secrets set DATABASE_PASSWORD="your-password"

# Set other secrets
fly secrets set REDIS_HOSTS="redis://your-redis-url:6379"
fly secrets set RESEND_API_KEY="re_your_api_key"
fly secrets set RESEND_FROM_EMAIL="noreply@yourdomain.com"
```

### 4. Verify Secrets

```bash
fly secrets list
```

### 5. Deploy

```bash
fly deploy
```

---

## Alternative: Use Fly.io Volumes (Not Recommended)

If you prefer using files:

1. Create a volume:
   ```bash
   fly volumes create jwt_keys --size 1
   ```

2. Update `fly.toml`:
   ```toml
   [mounts]
     source = "jwt_keys"
     destination = "/app/keys"
   ```

3. SSH into Fly.io app and create keys:
   ```bash
   fly ssh console
   mkdir -p /app/keys
   # Generate keys here
   ```

This is more complex and not recommended. Use environment variables instead.

---

## Fly.io Configuration File (fly.toml)

Create `fly.toml` in project root:

```toml
app = "your-app-name"
primary_region = "sin"  # Singapore, change as needed

[build]
  dockerfile = "Dockerfile"

[env]
  QUARKUS_HTTP_PORT = "8080"
  QUARKUS_HTTP_HOST = "0.0.0.0"
  APP_DOMAIN = "your-app.fly.dev"
  QUARKUS_FLYWAY_MIGRATE_AT_START = "true"

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = "suspend"
  auto_start_machines = true
  min_machines_running = 0

  [http_service.concurrency]
    type = "requests"
    soft_limit = 200
    hard_limit = 250

[[vm]]
  memory = "1gb"
  cpu_kind = "shared"
  cpus = 1
```

---

## Deployment Checklist

- [ ] Generate JWT keys locally
- [ ] Convert private key to single-line format
- [ ] Set all Fly.io secrets
- [ ] Create `fly.toml` configuration
- [ ] Test build: `docker build -t vno-backend .`
- [ ] Deploy: `fly deploy`
- [ ] Verify: `fly logs`
- [ ] Test API: `curl https://your-app.fly.dev/q/health`

---

## Troubleshooting

### JWT Key Error

If you see `SRJWT05021: Please set 'smallrye.jwt.sign.key.location' or 'smallrye.jwt.sign.key' property`:

```bash
# Verify secret is set
fly secrets list | grep JWT

# Check secret format (should be single line with \n)
fly ssh console
echo $SMALLRYE_JWT_SIGN_KEY
```

### Database Connection Error

```bash
# Verify database secrets
fly secrets list | grep DATABASE

# Test connection from Fly.io
fly ssh console
curl your-neon-url:5432
```

### App Crashes on Start

```bash
# Check logs
fly logs

# Check app status
fly status

# Restart app
fly apps restart
```

---

## Scaling & Monitoring

```bash
# Scale to multiple instances
fly scale count 2

# Scale memory
fly scale memory 2048

# View metrics
fly dashboard

# View logs in real-time
fly logs -f
```

---

## Local Testing with Environment Variables

Before deploying, test locally:

```bash
# Set environment variable (PowerShell)
$env:SMALLRYE_JWT_SIGN_KEY = Get-Content privateKey.pem -Raw

# Run backend
./gradlew quarkusDev
```

---

## Security Best Practices

1. **Never commit keys** to Git:
   ```bash
   # Add to .gitignore
   *.pem
   privateKey.*
   publicKey.*
   ```

2. **Rotate keys periodically**:
   ```bash
   # Generate new keys
   openssl genrsa -out newPrivateKey.pem 2048
   
   # Update secret
   fly secrets set SMALLRYE_JWT_SIGN_KEY="$(cat newPrivateKey.pem)"
   ```

3. **Use different keys for production and staging**

4. **Backup your keys securely** (1Password, AWS Secrets Manager, etc.)
