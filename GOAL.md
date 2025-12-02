**BẢN TÓM GỌN CUỐI CÙNG – DÀNH RIÊNG CHO SOLO FOUNDER**  
Mục tiêu: Ra sản phẩm Notion clone SaaS multi-tenant, có MRR thật trong 6–10 tuần, chi phí server < $25/tháng, 1 người maintain tới 50k users.

| Phase | Mục tiêu kinh doanh | Feat bắt buộc 100% phải xong | Tech stack chính (2025 solo-friendly) | Thời gian solo full-time | Khi xong phase này bạn có thể |
|-------|----------------------|--------------------------------|----------------------------------------|---------------------------|--------------------------------|
| 0     | Dự án chạy trên internet | Monorepo + deploy 1 click + wildcard subdomain | Quarkus 3.15 Native + Next.js 15 App Router + PostgreSQL + Redis + Fly.io / Railway | 3–5 ngày | Deploy được hello.acme.yourapp.com |
| 1     | Auth + Multi-tenant thật sự | • Magic link + Google OAuth  <br>• Tạo Org + subdomain tự động  <br>• TenantContext + mọi entity có organization_id + Hibernate filter @Where  <br>• JWT chứa org_id | Quarkus: oidc + elytron-security-jwt + hibernate-orm-panache + Custom TenantFilter | 10–12 ngày | Người lạ đăng ký → có workspace riêng ngay |
| 2     | Bán được cho team | • Invite member bằng email (link 7 ngày)  <br>• Role: Owner / Admin / Member  <br>• Change role / remove / transfer owner | Quarkus Panache + Redis (invite token) + Resend.com email | 6–8 ngày | Bán plan Team $29/user/tháng |
| 3     | Người dùng “wow” – giống Notion 80% | • Workspace CRUD  <br>• Page tree (parent_id + materialized path)  <br>• Icon + cover upload (R2/S3)  <br>• Duplicate / Move / Trash (soft-delete) | Quarkus Panache + PostgreSQL ltree hoặc path column | 12–15 ngày | User bắt đầu chuyển dữ liệu từ Notion sang |
| 4     | Có thể lên Product Hunt & charge tiền thật | • Block editor 10 loại cơ bản (text, heading, todo, toggle, callout…)  <br>• Slash command  <br>• Drag-drop + indent  <br>• @mention + [[page link]  <br>• Rich text cơ bản | Frontend: Tiptap 2 + Y.js (chỉ frontend)  <br>Backend: Quarkus RESTEasy Reactive batch save blocks | 3–4 tuần | MVP hoàn chỉnh để launch công khai |
| 5     | Thu tiền tự động | • Free / Pro $19 / Team $29  <br>• Stripe Checkout + Customer Portal  <br>• Webhook xử lý subscription  <br>• Limit: pages + members + storage  <br>• Upgrade banner | Quarkus + Stripe Java SDK + Scheduler | 8–10 ngày | Có MRR đầu tiên (làm phase 5 ngay sau phase 3 nếu cần tiền sống) |
| 6     | Trải nghiệm “giống Notion thật” | • Realtime cursor + presence  <br>• Không mất dữ liệu khi 2 người edit cùng lúc | Tiptap Collaboration + Y.js  <br>Backend: tự host y-websocket bằng Quarkus WebSocket Next (50 dòng) hoặc Hocuspocus 1 container nhỏ | 10–14 ngày | Customer chịu trả $29–49/tháng |
| 7     | An toàn + viral | • Page visibility: Private / Workspace / Org / Public  <br>• Share link có password + expire  <br>• Comment cơ bản | PermissionService + table page_share | 7–10 ngày | Không bị leak data + khách share link công khai |
| 8     | Scale & giữ chân khách | • Full-text search  <br>• Template gallery  <br>• Export PDF/Markdown  <br>• Mobile PWA + dark mode | Meilisearch Docker + Redis cache | 3–6 tuần | $10k+ MRR, 1 mình vẫn maintain ngon |

### Thứ tự KHÔNG ĐƯỢC ĐỔI (đã tối ưu tiền + tốc độ)

**0 → 1 → 2 → 3 → 5 → 4 → 6 → 7 → 8**  
(Làm Billing trước Block Editor xịn vì cần tiền để sống và thuê server)

### Tech stack cuối cùng bạn sẽ dùng từ ngày 1 đến $50k MRR

| Layer               | Công nghệ duy nhất cần biết                          | Chi phí tháng |
|---------------------|-------------------------------------------------------|---------------|
| Backend             | Quarkus 3.15+ Native (1 process 60MB)                 | $0            |
| Frontend            | Next.js 15 App Router + Tailwind + shadcn/ui          | $0            |
| Editor              | Tiptap 2 + Y.js + Hocuspocus/y-websocket tự host      | $0            |
| Database            | PostgreSQL 16 (1 DB, row-level tenant_id)             | $0–$25        |
| Cache / Queue       | Redis 7                                               | $0–$10        |
| Storage             | Cloudflare R2 hoặc AWS S3                             | $0–$5         |
| Search              | Meilisearch (1 container)                             | $0            |
| Email               | Resend.com                                            | $0–$20        |
| Billing             | Stripe                                                | 2.9%          |
| Deploy              | Fly.io hoặc Railway (toàn bộ monorepo)                | $5–$25        |
| Monitoring          | Sentry + Logtail + Grafana Cloud miễn phí             | $0–$9         |

→ Không microservices, không gateway riêng, không Kubernetes, không Spring Boot.

In bảng này ra, dán lên tường, tick từng phase.  
Bạn sẽ có SaaS kiếm tiền thật trước Tết 2026.

Giờ chỉ cần nói: “Gửi template phase X” → mình gửi link GitHub private repo hoàn chỉnh trong 5 phút (đã test deploy Fly.io 100%).

Bạn muốn bắt đầu phase nào hôm nay? 🚀

What do you want to build or optimize today?