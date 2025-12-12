# Backend Checklist (Quarkus + Gradle Kotlin + Java 21)

Owner: you
Scope: Phase 0 → 1 aligned with GOAL.md

## Decisions (locked)
- [x] Deploy target: Fly.io
- [x] Primary domain: *.vno.com (wildcard subdomain)
- [x] Subdomain format: orgslug.vno.com
- [x] Container registry: GitHub Container Registry (ghcr.io)
- [x] Database: Neon Postgres
- [x] Redis: Upstash
- [x] Email: Resend.com
- [x] Google OAuth: will provide client_id/client_secret later
- [x] Default org slug: generated from user email domain

## Milestones
- [ ] Phase 0: Project scaffold + 1-click deploy + wildcard subdomain works
- [ ] Phase 1: Auth (magic link + Google) + true multi-tenant isolation

---

## Phase 0 — Baseline & Deployability

### Repo & Build
- [x] Initialize Gradle Kotlin DSL project (Java 21)
- [x] Add Quarkus platform BOM + quarkus-gradle-plugin
- [ ] Version catalog (libs.versions.toml)
- [x] Testing stack: JUnit5, RestAssured
- [ ] Code quality: spotless/ktlint (optional)

### Quarkus Extensions
- [x] REST: quarkus-resteasy-reactive, quarkus-smallrye-openapi
- [x] Data: quarkus-hibernate-orm-panache, quarkus-jdbc-postgresql, quarkus-flyway
- [ ] Security/JWT: quarkus-elytron-security-jwt (base), quarkus-oidc (for Phase 1)
- [x] Cache: quarkus-redis-client
- [x] Health/Metrics: quarkus-smallrye-health, quarkus-micrometer

### Configuration
- [ ] application-dev.properties
- [ ] application-test.properties
- [ ] application-prod.properties (env-driven)
- [x] CORS, JSON, OpenAPI config
- [x] Logging (request/response basic)

### Database & Migrations
- [x] Configure Neon Postgres JDBC url
- [x] Flyway V1__init.sql present (empty or with base tables if ready)
- [ ] Connection pool sizing (prod)

### Redis
- [x] Configure Upstash Redis URL and credentials

### Containerization & Deploy
- [x] Dockerfile JVM
- [ ] Optional: native profile (GraalVM/Mandrel) later
- [x] Healthcheck endpoints exposed
- [ ] GitHub Actions: build + test + build image + push to ghcr.io
- [x] Fly.io app config (fly.toml)
- [ ] Secrets wiring on Fly.io (see Env Secrets)
- [ ] Provision Neon DB (project + branch)
- [ ] Provision Upstash Redis
- [ ] DNS: create wildcard CNAME/ALIAS for *.vno.com → Fly hostname
- [ ] Verify: GET https://hello.acme.vno.com/api/health returns 200

### Wildcard Subdomain Test
- [x] Implement simple endpoint that echoes parsed org slug from host
- [ ] Ensure reverse proxies forward Host header

---

## Phase 1 — Auth + Multi-tenant (True)

### Data Model (Flyway V2)
- [x] organizations(id, slug unique, name, created_at)
- [x] users(id, email unique, name, avatar, created_at)
- [x] memberships(id, organization_id fk, user_id fk, role enum[OWNER,ADMIN,MEMBER], unique(org,user))
- [x] invitations(id, organization_id fk, email, token, expires_at, accepted_at)
- [x] workspaces(id, organization_id fk, name, created_at)
- [x] pages(id, organization_id fk, workspace_id fk, parent_id fk, title, path/materialized_path, is_deleted, created_at, updated_at)
- [x] Indices and FKs created

### Tenant Isolation
- [~] Parse subdomain to resolve org slug (REMOVED: JWT-only)
- [x] Lookup org_id by slug, set TenantContext per request
- [x] Base entity includes organization_id
- [x] Apply Hibernate filter/@Where to enforce org_id automatically
- [x] Ensure all queries go through Panache repos with tenant context

### Auth: Magic Link
- [x] POST /auth/magic-link → generate token (Redis TTL), send email via Resend
- [x] GET /auth/callback?token=... → validate, upsert user, bootstrap org if new
- [x] Issue JWT including org_id claim, expiry, refresh rules

### Auth: Google OAuth (OIDC)
- [x] Configure OIDC client (Google) in application properties
- [ ] Callback endpoint → same bootstrap flow, issue JWT
- [ ] Map Google email → user record, derive default org slug from email domain

### Org Bootstrap
- [x] If user first-time: create organization (unique slug), membership OWNER
- [x] Create default workspace
- [x] Reserve subdomain (orgslug.vno.com)

### Roles & Membership (basic)
- [x] Role enum: OWNER/ADMIN/MEMBER
- [x] Enforce with security annotations/interceptors

### API Surface (minimal)
- [x] GET /health, /ready
- [x] GET /me (requires JWT)
- [x] GET/POST /org
- [x] GET/POST /workspaces
- [x] GET/POST /pages (basic CRUD; soft delete flag present)
- [ ] Error handling: exception mappers, validation errors

### Observability
- [x] Metrics enabled
- [x] Request logging
- [ ] Optional Sentry DSN wired

---

## Environment & Secrets
Set in GitHub Actions and Fly.io secrets.

- [ ] DATABASE_URL (Neon)
- [ ] DATABASE_USER
- [ ] DATABASE_PASSWORD
- [ ] REDIS_URL (Upstash)
- [ ] RESEND_API_KEY
- [ ] GOOGLE_CLIENT_ID (later)
- [ ] GOOGLE_CLIENT_SECRET (later)
- [ ] OAUTH_REDIRECT_URI (later)
- [ ] JWT_SIGN_KEY (HS256) or JWKS URL
- [ ] APP_DOMAIN = vno.com

---

## Validation Criteria
- [ ] Phase 0: 1-click CI → Fly deploy green, wildcard host works
- [ ] Phase 1: Magic link + Google login end-to-end, tenant isolation by org_id

---

## Notes / Links
- Neon: https://neon.com
- Upstash: https://upstash.com
- Resend: https://resend.com
- Fly.io: https://fly.io
- GHCR: https://ghcr.io
