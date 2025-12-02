```
src/main/java/com.vno/
├── core/          ← TenantContext, PermissionService, AuditLog
├── auth/          ← Login, MagicLink, JWT, Google OAuth
├── org/           ← Organization, Member, Invite, Team
├── workspace/     ← Workspace, Page tree, Trash
├── editor/        ← Block, Version, Realtime gateway
├── billing/       ← Stripe, Plan, Usage, Webhook
├── file/          ← Upload, S3 presigned URL
├── share/         ← Public link, Permission override
├── search/        ← Sync job → Meilisearch
└── admin/         ← Dashboard, Org lookup (chỉ Owner của bạn)
```

### core/ – Nền móng sống còn (Phase 1)
- `TenantContext` (ThreadLocal + InheritableThreadLocal)
- `CurrentTenantIdentifierResolver` (Hibernate multi-tenant)
- `MultiTenantFilter` (đọc subdomain → set orgId)
- `PermissionService.checkCanRead/WritePage()`
- `AuditLogService.log(action, entity, metadata)`
- Base entity `AbstractAuditingEntity` (createdBy, updatedBy, organizationId, deletedAt)
- Global exception handler → JSON lỗi đẹp + audit log
- Bucket4j rate limit fallback (khi Cloudflare không đủ)

### auth/ — (Phase 1)
- Google OAuth2 login (quark ուక-oidc)
- Magic link (POST /auth/magic → GET: send email → GET /verify?token=)
- JWT issue + refresh (contain org_id + role)
- `AuthService.login(), switchOrg(orgId)`
- `SecurityConfig` với @RolesAllowed + custom Voter (org role)
- Passwordless hoàn toàn → users: có bảng users.password_hash = NULL

### org/ — (Phase 2)
- Organization CRUD (tạo subdomain slug: slug + random nếu trùng)
- Invite (email + token + role + expires 7 ngày)
- `MemberService.addMember(email, role)`
- `OrganizationService.updateMemberRole(), removeMember(), transferOwnership():`
- Invite token blacklist (Redis)
- Team model (tối ưu sau này, MVP không cần)

### workspace/ — (Phase 3)
- Workspace CRUD + icon/cover
- Page tree (parent_id + materialized path LTree)
- `PageService.movePage(pageId, newParentId, newWorkspaceId)`
- `PageService.duplicatePage(pageId)` → deep copy blocks
- Trash (soft-delete + restore + cron xóa vĩnh viễn 30 ngày)
- Page lock (chỉ owner mở khóa)

### editor/ — (Phase 4 – nặng nhất)
- Block entity (type + JSONB content + order_index)
- `BlockService.saveBatch(pageId, List<BlockDto>)` → upsert + reorder
- Version history (table block_version hoặc Y.js snapshot sau này)
- Realtime gateway (WebSocket hoặc Server-Sent Events) → forward Y.js messages
- `@mention` resolver (tìm user trong org)
- `[[page]]` link resolver (search page title)

### billing/ — (Phase 5 – cần tiền để sống)
- Stripe Checkout Session + Customer Portal
- Webhook handler (invoice.paid, subscription.updated, deleted)
- `SubscriptionService.getCurrentPlan(orgId)`
- Usage limiter (pages_count, members_count, storage_bytes)
- Upgrade banner + limit gate (modal khi vượt)

### file/ — (Phase 3+)
- Presigned URL generator (R2/S3)
- File metadata (size, mime, url)
- Virus scan hook (ClamAV hoặc Cloudflare)
- Auto-cleanup khi delete page/cover

### share/ — (Phase 7 – viral)
- `ShareService.createPublicLink(pageId, permission, password?, expires?)`
- Public page endpoint `/s/{token}` (không cần login)
- Password protection + rate limit (10 lần sai → block 1h)
- Share analytics (view count, last viewed)

### search/ — (Phase 8)
- Sync job (cron mỗi 5 phút) → push page.title + block.text → Meilisearch
- Index per org → lọc theo subdomain (: (: (Meilisearch filter)
- Search API `/api/search?q=`

### admin/ — (Chỉ bạn dùng – bật bằng IP hoặc secret header)
- Dashboard: Tổng org, MRR tháng này, top 10 org usage
- Org lookup: Gõ subdomain → xem tất cả (members, pages, logs, plan)
- Manual override: Change plan, reset usage, delete org
- Raw audit log search (by org_id, user_id, action)

### Bonus: Các job cần có (Quarkus @Scheduled)
```java
@Scheduled(every = "1h")  void cleanupExpiredInvites();
@Scheduled(every = "24h") void cleanupTrash();
@Scheduled(every = "5m")  void syncToMeilisearch();
@Scheduled(every = "24h") void calculateDailyUsage();
@Scheduled(cron = "midnight:30 2 * JAN,APR,JUL,OCT *")
void sendBillingReminder();
```

### Tổng kết: Bạn chỉ cần làm đúng thứ tự này
1. core + auth (2 tuần)
2. org + workspace (2 tuần)
3. billing (1 tuần) → có tiền
4. editor cơ bản + file (3–4 tuần)
5. share + admin + search (2–3 tuần)
6. realtime (2 tuần)

---

## Local Development (Dev Mode)

### Prerequisites
- JDK 21
- Gradle 8+ installed (no wrapper in repo)

### Run in dev mode
```bash
gradle quarkusDev
```

Environment variables (optional for basic endpoints):
- `PORT=8080` (default)
- `APP_DOMAIN=vno.com`
- `DATABASE_URL`, `REDIS_URL` can be left empty for Phase 0 endpoints.

When started, the app listens on `http://localhost:8080`.

### Test endpoints
- Health check
```bash
curl http://localhost:8080/api/health
```

- Wildcard subdomain echo (send Host header)
```bash
curl -H "Host: hello.vno.com" http://localhost:8080/api/whoami
```

On Windows PowerShell, the same commands work with built-in `curl` alias.

### Run tests
```bash
gradle test
```

### Build runnable JAR (non-dev)
```bash
gradle build -x test
```
