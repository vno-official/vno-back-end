# VNO Backend

A modern, multi-tenant SaaS backend built with Quarkus, featuring JWT authentication, refresh tokens, and organization-based access control.

## 🚀 Features

- **Multi-tenancy**: Organization-based isolation with tenant context
- **Authentication & Authorization**: JWT with refresh tokens, role-based access control
- **Reactive Architecture**: Built on Quarkus with Hibernate Reactive and Mutiny
- **Modern Stack**: PostgreSQL, Redis, Reactive programming
- **API Documentation**: Interactive Swagger UI
- **Security**: BCrypt password hashing, token rotation, secure session management

## 📋 Tech Stack

- **Framework**: Quarkus 3.x
- **Language**: Java 21
- **Database**: PostgreSQL (with Hibernate Reactive)
- **Cache**: Redis
- **Authentication**: JWT (MicroProfile JWT), OAuth2 (Google)
- **Email**: Resend
- **Build**: Gradle (Kotlin DSL)
- **Migration**: Flyway

## 🏗️ Architecture

```
├── src/main/java/com/vno
│   ├── auth/              # Authentication & Authorization
│   │   ├── dto/           # Request/Response DTOs
│   │   ├── email/         # Email templates
│   │   ├── service/       # Auth business logic
│   │   └── web/           # REST endpoints
│   ├── core/              # Core domain entities
│   │   ├── entity/        # JPA entities (User, Organization, etc.)
│   │   ├── security/      # Permission checks
│   │   └── tenant/        # Multi-tenancy infrastructure
│   ├── org/               # Organization management
│   ├── workspace/         # Workspace management
│   └── page/              # Page/Block management
└── src/main/resources
    ├── db/migration/      # Flyway migrations
    └── application.yml    # Configuration
```

## 🔐 Authentication Flow

### 1. Register/Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response:**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "uuid...",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "user@example.com",
    "avatarUrl": "https://..."
  },
  "tenant": {
    "id": "uuid",
    "name": "Company Name",
    "plan": "pro"
  },
  "organization": {
    "id": "uuid",
    "name": "Main Office",
    "code": "MAIN",
    "role": "OWNER",
    "permissions": ["*"]
  }
}
```

### 2. Use Access Token
```bash
GET /api/workspaces
Authorization: Bearer {accessToken}
```

### 3. Refresh Access Token
```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "uuid..."
}
```

**Response:**
```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### 4. Logout (Revoke Refresh Token)
```bash
POST /api/auth/revoke
Content-Type: application/json

{
  "refreshToken": "uuid..."
}
```

## 🔑 JWT Structure

The access token contains complete user and organization data:

```json
{
  "iss": "vno-backend",
  "sub": "user-uuid",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "John Doe",
    "avatarUrl": "https://...",
    "createdAt": "2024-..."
  },
  "currentOrganization": {
    "id": "uuid",
    "slug": "company",
    "name": "Company Name",
    "plan": "pro",
    "createdAt": "2024-..."
  },
  "organizations": [
    {
      "id": "uuid",
      "slug": "company",
      "name": "Company Name",
      "plan": "pro",
      "createdAt": "2024-...",
      "role": "OWNER"
    }
  ],
  "role": "OWNER",
  "groups": ["OWNER"],
  "exp": 1234567890,
  "iat": 1234567890
}
```

## 🗄️ Database Schema

### Core Entities

**users**
- `id` (UUID, PK)
- `email` (VARCHAR, unique)
- `password_hash` (VARCHAR)
- `name` (VARCHAR)
- `avatar_url` (VARCHAR)
- `created_at` (TIMESTAMP)

**organizations**
- `id` (UUID, PK)
- `name` (VARCHAR)
- `slug` (VARCHAR, unique)
- `plan` (VARCHAR)
- `created_at` (TIMESTAMP)

**user_organizations**
- `id` (UUID, PK)
- `user_id` (UUID, FK → users)
- `organization_id` (UUID, FK → organizations)
- `role` (VARCHAR: OWNER, ADMIN, MEMBER)

**refresh_tokens**
- `id` (UUID, PK)
- `user_id` (UUID, FK → users)
- `token` (VARCHAR, unique)
- `expires_at` (TIMESTAMP)
- `revoked_at` (TIMESTAMP)
- `last_used_at` (TIMESTAMP)

## ⚙️ Configuration

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/vno
DATABASE_REACTIVE_URL=postgresql://localhost:5432/vno
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password

# Redis
REDIS_HOSTS=redis://localhost:6379

# JWT
APP_JWT_ACCESS_TOKEN_EXPIRY_MINUTES=15
APP_JWT_REFRESH_TOKEN_EXPIRY_DAYS=7

# Email (Resend)
RESEND_API_KEY=re_...
RESEND_FROM_EMAIL=noreply@example.com
RESEND_FROM_NAME=VNO

# Google OAuth (optional)
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...

# App
APP_DOMAIN=vno.com
```

### application.yml

See [application.yml](src/main/resources/application.yml) for full configuration.

## 🚦 Getting Started

### Prerequisites

- Java 21+
- PostgreSQL 14+
- Redis 7+
- Gradle 8.5+

### Local Development

1. **Clone and setup**
```bash
git clone <repository>
cd vno-backend
```

2. **Configure database**
```bash
createdb vno
```

3. **Set environment variables**
```bash
cp .env.example .env
# Edit .env with your values
```

4. **Run development server**
```bash
./gradlew quarkusDev
```

The application will start on `http://localhost:8080`

### API Documentation

Visit Swagger UI: `http://localhost:8080/q/swagger-ui`

### Health Check

```bash
curl http://localhost:8080/q/health
```

## 📝 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/revoke` - Logout (revoke refresh token)
- `POST /api/auth/change-password` - Change password (authenticated)
- `POST /api/auth/request-reset` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token
- `POST /api/auth/switch-org` - Switch to different organization

### Organizations
- `GET /api/orgs` - List user's organizations
- `POST /api/orgs` - Create new organization
- `GET /api/orgs/{id}` - Get organization details
- `PUT /api/orgs/{id}` - Update organization
- `DELETE /api/orgs/{id}` - Delete organization

### Workspaces
- `GET /api/workspaces` - List workspaces
- `POST /api/workspaces` - Create workspace
- `GET /api/workspaces/{id}` - Get workspace
- `PUT /api/workspaces/{id}` - Update workspace
- `DELETE /api/workspaces/{id}` - Delete workspace

### Pages
- `GET /api/workspaces/{id}/pages` - List pages in workspace
- `POST /api/workspaces/{id}/pages` - Create page
- `GET /api/pages/{id}` - Get page with blocks
- `PUT /api/pages/{id}` - Update page
- `DELETE /api/pages/{id}` - Delete page

## 🔒 Security Features

### Password Security
- BCrypt hashing with work factor 12
- Minimum 8 character requirement
- Password history (prevents reuse)

### Token Security
- Short-lived access tokens (15 minutes)
- Long-lived refresh tokens (7 days)
- Server-side token storage
- Token rotation on refresh
- Revocation support

### Multi-tenancy
- Organization-based data isolation
- Tenant context propagation
- Row-level security via Hibernate filters

### Authorization
- Role-based access control (OWNER, ADMIN, MEMBER)
- Permission checks for all operations
- Private workspace enforcement

## 🧪 Testing

### Run tests
```bash
./gradlew test
```

### Test with Swagger UI
1. Navigate to `http://localhost:8080/q/swagger-ui`
2. Click "Authorize" button
3. Login to get access token
4. Paste token in authorization modal
5. Test endpoints interactively

### Manual Testing

See [TESTING.md](TESTING.md) for detailed test scenarios.

## 🐳 Docker

### Build image
```bash
./gradlew build
docker build -t vno-backend .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## 📊 Monitoring

### Health Checks
- `/q/health` - Overall health
- `/q/health/live` - Liveness probe
- `/q/health/ready` - Readiness probe

### Metrics
- `/q/metrics` - Prometheus metrics

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is proprietary software.

## 🔗 Links

- [Quarkus Documentation](https://quarkus.io/guides/)
- [API Documentation](http://localhost:8080/q/swagger-ui)
- [Health Dashboard](http://localhost:8080/q/health-ui)

## 📞 Support

For support, email support@vno.com or join our Slack channel.
