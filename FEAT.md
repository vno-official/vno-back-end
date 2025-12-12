# Features & Architecture (VNO Backend)

> **Goal:** A production-ready, multi-tenant SaaS backend (Notion clone) designed for a solo founder.  
> **Status:** Phase 0 (Baseline) & Phase 1 (Auth + Multi-tenancy) Complete.

---

## 1. Core Architecture

### **True Multi-Tenancy**
- **Isolation Level:** Database Row-Level Security (Application Layer).
- **Implementation:** 
  - Every entity implements `StandardEntity` having `organization_id`.
  - **Hibernate Filter / `@Where`**: Automatically enforces `organization_id` on all queries based on the current context.
  - **TenantContext**: Thread-local / Request-scoped context that resolves the current organization from the subdomain/JWT.
- **Routing:** 
  - **JWT-based:** Tenant is resolved strictly via `org_id` claim in the Bearer token.

### **Tech Stack (Reactive)**
- **Framework:** Quarkus 3.x (Native ready).
- **Language:** Java 21 + Gradle Kotlin DSL.
- **Database:** PostgreSQL 16 (Neon.tech) with Reactive driver.
- **ORM:** Hibernate Reactive with Panache.
- **Asynchronous:** Fully non-blocking I/O using Mutiny (`Uni`, `Multi`).

### **Data Identity (Modern Standard)**
- **UUID v7:** All primary keys use UUID v7 (time-sortable, database-friendly) instead of random UUID v4 or simple Long IDs.

---

## 2. Authentication & Security

### **Auth Flows**
- **Magic Link:** Passwordless login via Email (Resend).
  - Flow: Request Link -> Email -> Verify Token -> issue JWT.
- **Google OAuth:** OIDC integration configured for social login.
- **JWT (Stateless):**
  - Tokens contain `org_id` claim to specific tenant context.
  - Automatic Organization context switching logic.

### **Organization Bootstrap**
- **First-time Login:** Automatically creates:
  - A new **Organization** (slug derived from email domain or random).
  - A default **Workspace**.
  - Assigns **OWNER** role.

### **Permissions**
- **User Roles:** `OWNER`, `ADMIN`, `MEMBER`.
- **Row-Level Access:** Strict isolation ensures users can never access data outside their active Organization context.

---

## 3. Infrastructure & Deployment

### **Deployment**
- **Platform:** Fly.io.
- **Container:** Docker (JVM mode optimized, Native planned).
- **CI/CD:** GitHub Actions -> GitHub Container Registry (ghcr.io).
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) with unified version management.

### **External Integrations**
- **Database:** Neon Serverless Postgres.
- **Cache:** Upstash Redis (for session tokens, magic links, cache).
- **Email:** Resend (Transactional emails for invites/login).

---

## 4. Feature Modules

### **Workspace Management**
- **Workspaces:** Logical grouping of pages within an Organization.
- **Full CRUD:** Create, Read, Update, Delete workspaces.

### **Page System**
- **Hierarchical Structure:** Implementation of Page Tree (Parent/Child relationships).
- **Materialized Path:** Optimized querying for deep hierarchies (planned/partial implementation).
- **Soft Delete:** Trash bin functionality (pages are marked `is_deleted` rather than hard removed).

### **User & Team**
- **Profile:** Basic user profile management.
- **Invitations:** Invite members to Organization via email options.

---

## 5. Developer Experience / Observability

- **API Documentation:** OpenAPI / Swagger UI implemented (`/q/swagger-ui`).
  - **Bearer Auth Support:** Integrated directly into Swagger UI for easy testing.
- **Metrics:** Micrometer metrics exposed.
- **Health Checks:** Liveness/Readiness probes (`/q/health`) for orchestration.
- **Local Dev:** Docker Compose ready for full local stack (Postgres, Redis).
