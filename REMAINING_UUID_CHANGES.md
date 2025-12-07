# Remaining Files to Update for UUID Migration

## Critical Files with Compilation Errors

### 1. AuthService.java
**Location**: `src/main/java/com/vno/auth/service/AuthService.java`
**Changes Needed**:
- Change `switchOrganization(Long userId, Long targetOrgId)` to `switchOrganization(UUID userId, UUID targetOrgId)`
- Update anywhere parsing user ID from JWT: change from Long.parseLong() to UUID.fromString()

### 2. AuthResource.java  
**Location**: `src/main/java/com/vno/auth/web/AuthResource.java`
**Changes Needed**:
- In `getCurrentUser()`: Parse UUID from JWT claims
  - Change `Long userId = Long.parseLong(jwt.getSubject())` to `UUID userId = UUID.fromString(jwt.getSubject())`
  - Change `Long currentOrgId = jwt.getClaim("org_id")` to `String orgIdStr = jwt.getClaim("org_id"); UUID currentOrgId = UUID.fromString(orgIdStr)`
- In `switchOrganization()`: Change `@QueryParam("orgId") Long orgId` to `@QueryParam("orgId") UUID orgId`

### 3. OrgBootstrapService.java
**Location**: `src/main/java/com/vno/org/service/OrgBootstrapService.java`
**Changes Needed**:
- All organization ID handling should use UUID instead of Long

### 4. WorkspaceResource.java
**Location**: `src/main/java/com/vno/workspace/web/WorkspaceResource.java`
**Changes Needed**:
- Change all `@PathParam Long id` to `@PathParam UUID id`
- Change all `@QueryParam Long workspaceId` to `@QueryParam UUID workspaceId`
- Parse org ID from JWT as UUID instead of Long

### 5. PageResource.java
**Location**: `src/main/java/com/vno/workspace/web/PageResource.java`
**Changes Needed**:
- Change all `@PathParam Long id` to `@PathParam UUID id`
- Change all `@PathParam Long workspaceId` to `@PathParam UUID workspaceId`
- Change all `@PathParam Long pageId` to `@PathParam UUID pageId`
- Parse org ID from JWT as UUID instead of Long

### 6. BlockResource.java
**Location**: `src/main/java/com/vno/editor/web/BlockResource.java`
**Changes Needed**:
- Change `@QueryParam Long pageId` to `@QueryParam UUID pageId`
- Parse org ID from JWT as UUID instead of Long

### 7. OrgResource.java
**Location**: `src/main/java/com/vno/org/web/OrgResource.java`
**Changes Needed**:
- Change all `@PathParam Long id` to `@PathParam UUID id`
- Parse org ID from JWT as UUID instead of Long

### 8. PermissionService.java
**Location**: `src/main/java/com/vno/core/security/PermissionService.java`
**Changes Needed**:
- Update all organization ID handling to use UUID

### 9. TenantFilter.java
**Location**: `src/main/java/com/vno/core/tenant/TenantFilter.java`
**Changes Needed**:
- Parse org ID from JWT as UUID: `UUID orgId = UUID.fromString(jwt.getClaim("org_id"))`

## Pattern for Parsing JWT Claims

Since JWT claims are strings when dealing with UUIDs, always parse them:

```java
// OLD - Long
Long userId = Long.parseLong(jwt.getSubject());
Long orgId = jwt.getClaim("org_id");

// NEW - UUID (note: claims are strings for UUIDs)
UUID userId = UUID.fromString(jwt.getSubject());  
String orgIdStr = jwt.getClaim("org_id");
UUID orgId = UUID.fromString(orgIdStr);
```

## Testing After Migration

1. Login endpoint: `POST /api/auth/login`
2. Verify JWT contains UUID strings in claims
3. Test getting blocks: `GET /api/blocks?pageId=40000000-0000-0000-0000-000000000001`
4. Verify all UUIDs in responses are proper UUID format

## Notes
- All primary keys and foreign keys are now UUID
- JWT claims store UUIDs as strings
- URL path parameters accept UUIDs directly (Spring/Quarkus handles conversion)
- Query parameters accept UUIDs directly
