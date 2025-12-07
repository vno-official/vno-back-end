# VNO Backend Architecture

## Overview

VNO is a multi-tenant SaaS platform built with modern reactive architecture using Quarkus framework.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                          │
│  (Web App, Mobile App, Third-party integrations)        │
└──────────────────┬──────────────────────────────────────┘
                   │
                   │ HTTPS/REST
                   │
┌──────────────────▼──────────────────────────────────────┐
│                  API Gateway (Optional)                  │
│           Load Balancer / Rate Limiting                  │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│              Application Layer (Quarkus)                 │
│                                                           │
│  ┌────────────────────────────────────────────────────┐ │
│  │         REST Controllers (JAX-RS)                   │ │
│  │  AuthResource │ OrgResource │ WorkspaceResource     │ │
│  └───────┬────────────────────────────────────────────┘ │
│          │                                                │
│  ┌───────▼────────────────────────────────────────────┐ │
│  │          Security & Filters                         │ │
│  │  JWT Auth │ Tenant Filter │ CORS                    │ │
│  └───────┬────────────────────────────────────────────┘ │
│          │                                                │
│  ┌───────▼────────────────────────────────────────────┐ │
│  │            Service Layer                            │ │
│  │  AuthService │ OrgService │ WorkspaceService        │ │
│  └───────┬────────────────────────────────────────────┘ │
│          │                                                │
│  ┌───────▼────────────────────────────────────────────┐ │
│  │         Repository Layer (Panache)                  │ │
│  │  User │ Organization │ Workspace │ Page             │ │
│  └───────┬────────────────────────────────────────────┘ │
└──────────┼──────────────────────────────────────────────┘
           │
    ┌──────┴──────┬──────────────┬────────────┐
    │             │              │            │
┌───▼────┐  ┌────▼────┐   ┌─────▼────┐  ┌──▼────┐
│PostgreSQL  │  Redis  │   │  Email   │  │ OAuth │
│ Database │  │  Cache  │   │ Service  │  │ (Google)│
└──────────┘  └─────────┘   └──────────┘  └───────┘
```

## Core Components

### 1. REST Layer (`web/`)
- **Purpose**: HTTP request handling, validation, response formatting
- **Technology**: JAX-RS (Jakarta REST)
- **Responsibilities**:
  - Request validation with Bean Validation
  - DTO transformation
  - HTTP status code handling
  - OpenAPI documentation

### 2. Service Layer (`service/`)
- **Purpose**: Business logic orchestration
- **Technology**: CDI (Jakarta EE)
- **Responsibilities**:
  - Transaction management (`@WithTransaction`)
  - Business rules enforcement
  - Service composition
  - Reactive flow orchestration (Mutiny)

### 3. Repository Layer (`entity/`)
- **Purpose**: Data access and persistence
- **Technology**: Hibernate Reactive Panache
- **Responsibilities**:
  - CRUD operations
  - Query composition
  - Entity lifecycle management
  - Tenant filtering

### 4. Security Layer
- **Purpose**: Authentication and authorization
- **Components**:
  - **JWT Authentication**: Token generation and validation
  - **Tenant Filter**: Multi-tenant data isolation
  - **Permission Service**: Role-based access control

## Multi-Tenancy Architecture

### Tenant Isolation Strategy

```
Request Flow with Tenant Context:

1. HTTP Request arrives
   ↓
2. TenantFilter extracts subdomain/org_id
   ↓
3. Set TenantContext (ThreadLocal)
   ↓
4. Hibernate Filter applied to all queries
   ↓
5. Only tenant's data accessible
   ↓
6. Response sent
   ↓
7. TenantContext cleared
```

### Implementation

**Tenant Context**
```java
public class TenantContext {
    private static ThreadLocal<UUID> currentTenantId = new ThreadLocal<>();
    
    public static void setTenantId(UUID tenantId);
    public static UUID getTenantId();
    public static void clear();
}
```

**Entity Filtering**
```java
@Entity
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "organization_id = :tenantId")
public class Workspace extends PanacheEntityBase {
    // Entity fields
}
```

## Authentication & Authorization

### JWT Flow

```
┌──────────┐                          ┌──────────────┐
│  Client  │                          │   Backend    │
└────┬─────┘                          └──────┬───────┘
     │                                       │
     │  POST /auth/login                     │
     │  {email, password}                    │
     ├──────────────────────────────────────>│
     │                                       │
     │                    ┌──────────────────┤
     │                    │ 1. Verify password
     │                    │ 2. Generate JWT    │
     │                    │ 3. Create refresh  │
     │                    └──────────────────>│
     │                                       │
     │  {accessToken, refreshToken, user}    │
     │<──────────────────────────────────────┤
     │                                       │
     │  GET /workspaces                      │
     │  Authorization: Bearer {JWT}          │
     ├──────────────────────────────────────>│
     │                                       │
     │                    ┌──────────────────┤
     │                    │ 1. Validate JWT
     │                    │ 2. Extract user/org
     │                    │ 3. Set tenant context
     │                    │ 4. Execute query  │
     │                    └──────────────────>│
     │                                       │
     │  {workspaces: [...]}                  │
     │<──────────────────────────────────────┤
     │                                       │
     │  POST /auth/refresh (when expired)    │
     │  {refreshToken}                       │
     ├──────────────────────────────────────>│
     │                                       │
     │  {access_token}                       │
     │<──────────────────────────────────────┤
```

### Role-Based Access Control (RBAC)

**Roles**:
- `OWNER`: Full access to organization
- `ADMIN`: Manage users, workspaces, pages
- `MEMBER`: View and edit assigned resources

**Permission Matrix**:

| Resource | OWNER | ADMIN | MEMBER |
|----------|-------|-------|--------|
| Create Organization | ✅ | ❌ | ❌ |
| Invite Users | ✅ | ✅ | ❌ |
| Create Workspace | ✅ | ✅ | ❌ |
| Edit Workspace | ✅ | ✅ | Owner only |
| Create Page | ✅ | ✅ | ✅ |
| Edit Page | ✅ | ✅ | Owner/Assignee |
| Delete Page | ✅ | ✅ | Owner only |

## Data Model

### Core Entities

```
┌──────────────┐       ┌────────────────────┐       ┌──────────────┐
│    User      │       │  UserOrganization  │       │ Organization │
├──────────────┤       ├────────────────────┤       ├──────────────┤
│ id (UUID)    │───┐   │ id (UUID)          │   ┌───│ id (UUID)    │
│ email        │   └──>│ user_id (FK)       │<──┘   │ name         │
│ password_hash│       │ organization_id(FK)│       │ slug         │
│ name         │       │ role (ENUM)        │       │ plan         │
│ avatar_url   │       └────────────────────┘       └──────────────┘
└──────────────┘                                            │
                                                            │
                    ┌───────────────────────────────────────┘
                    │
                    ▼
            ┌──────────────┐       ┌──────────────┐
            │  Workspace   │       │    Page      │
            ├──────────────┤       ├──────────────┤
            │ id (UUID)    │───┐   │ id (UUID)    │
            │ organization │   └──>│ workspace_id │
            │ name         │       │ title        │
            │ is_private   │       │ emoji        │
            └──────────────┘       │ parent_id    │
                                   └──────────────┘
                                           │
                                           ▼
                                   ┌──────────────┐
                                   │    Block     │
                                   ├──────────────┤
                                   │ id (UUID)    │
                                   │ page_id (FK) │
                                   │ type         │
                                   │ content(JSON)│
                                   │ order        │
                                   └──────────────┘
```

## Reactive Programming

Using **Mutiny** for reactive streams:

```java
// Reactive service method
@WithTransaction
public Uni<List<Workspace>> getWorkspaces() {
    return Workspace.findByOrganization(getCurrentOrgId())
        .flatMap(workspaces -> 
            // Load relationships reactively
            workspace.getPages()
                .map(pages -> {
                    workspace.pageCount = pages.size();
                    return workspace;
                })
        );
}
```

**Benefits**:
- Non-blocking I/O
- Better resource utilization
- Improved scalability
- Backpressure handling

## Caching Strategy

### Redis Cache

**Cached Data**:
- User sessions
- JWT blacklist (for logout)
- Organization metadata
- Frequently accessed workspaces

**Cache Pattern**:
```java
@Inject
RedisClient redis;

public Uni<Organization> getOrganization(UUID id) {
    String cacheKey = "org:" + id;
    
    return redis.get(cacheKey)
        .flatMap(cached -> {
            if (cached != null) {
                return Uni.createFrom().item(deserialize(cached));
            }
            return Organization.findById(id)
                .flatMap(org -> {
                    redis.setex(cacheKey, 3600, serialize(org));
                    return Uni.createFrom().item(org);
                });
        });
}
```

## Database Migrations

Using **Flyway** for version control:

```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_organizations.sql
├── V3__add_workspaces.sql
├── V4__add_pages_blocks.sql
├── V8__migrate_to_uuid.sql
├── V9__uuid_test_data.sql
├── V10__password_reset_tokens.sql
└── V11__refresh_tokens.sql
```

**Migration Rules**:
- Never modify existing migrations
- Always create new migration for changes
- Test migrations on copy of production data
- Include rollback scripts when possible

## Scalability Considerations

### Horizontal Scaling
- Stateless application design
- Session data in Redis (external)
- Database connection pooling
- Load balancer ready

### Performance Optimization
- Reactive non-blocking I/O
- Database query optimization
- Eager loading for N+1 prevention
- Redis caching layer
- CDN for static assets

### High Availability
- Multiple application instances
- Database replication (Primary/Replica)
- Redis Sentinel for cache HA
- Health checks and readiness probes

## Security Measures

1. **Authentication**: JWT with refresh tokens
2. **Authorization**: Role-based access control
3. **Data Isolation**: Multi-tenant filtering
4. **Password Security**: BCrypt hashing (work factor 12)
5. **Token Security**: Server-side storage, rotation
6. **Input Validation**: Bean Validation annotations
7. **SQL Injection**: Parameterized queries (Hibernate)
8. **XSS Protection**: Content Security Policy headers
9. **CORS**: Configured allowed origins
10. **Rate Limiting**: (Planned via API Gateway)

## Monitoring & Observability

### Health Checks
- `/q/health` - Overall health
- `/q/health/live` - Liveness probe
- `/q/health/ready` - Readiness probe

### Metrics
- `/q/metrics` - Prometheus format
- Request count, duration, errors
- Database connection pool stats
- JVM metrics

### Logging
- Structured JSON logging
- Log levels per package
- Request/Response logging (dev mode)
- Error tracking with stack traces

## Deployment Architecture

```
┌─────────────────────────────────────────────┐
│            Load Balancer (ALB/Nginx)         │
└──────┬─────────────────────┬─────────────────┘
       │                     │
   ┌───▼────┐           ┌────▼────┐
   │ App #1 │           │ App #2  │
   │ (Pod)  │           │ (Pod)   │
   └───┬────┘           └────┬────┘
       │                     │
       └──────────┬──────────┘
                  │
         ┌────────▼───────────┐
         │   PostgreSQL       │
         │ (RDS/Cloud SQL)    │
         └────────────────────┘
```

## Future Enhancements

1. **GraphQL API**: Alternative to REST
2. **WebSocket**: Real-time collaboration
3. **Event Sourcing**: Audit trail and versioning
4. **Microservices**: Split by bounded context
5. **Service Mesh**: Istio for advanced traffic management
6. **ElasticSearch**: Full-text search for pages/blocks
