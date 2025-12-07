# API Testing Guide

This guide provides detailed testing scenarios for all API endpoints.

## Prerequisites

- Backend running on `http://localhost:8080`
- Test user: `john@acme.com` / `Password123!`

## Authentication Tests

### 1. Register New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "SecurePass123!",
    "name": "New User"
  }'
```

**Expected Response (200 OK):**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "uuid...",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "name": "New User",
    "email": "newuser@example.com",
    "avatarUrl": null
  },
  "tenant": {
    "id": "uuid",
    "name": "New User's Organization",
    "plan": "free"
  },
  "organization": {
    "id": "uuid",
    "name": "New User's Organization",
    "code": "NEWUSERSORGA",
    "role": "OWNER",
    "permissions": ["*"]
  }
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@acme.com",
    "password": "Password123!"
  }'
```

### 3. Refresh Access Token

```bash
# Save refresh token from login response
REFRESH_TOKEN="<refresh-token-from-login>"

curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

**Expected Response:**
```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### 4. Change Password

```bash
# Use access token from login
ACCESS_TOKEN="<access-token>"

curl -X POST http://localhost:8080/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "currentPassword": "Password123!",
    "newPassword": "NewSecurePass456!"
  }'
```

### 5. Request Password Reset

```bash
curl -X POST http://localhost:8080/api/auth/request-reset \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@acme.com"
  }'
```

**Note**: Check email for reset link (or check logs in development mode)

### 6. Reset Password

```bash
# Use token from email
RESET_TOKEN="<token-from-email>"

curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d "{
    \"token\": \"$RESET_TOKEN\",
    \"newPassword\": \"ResetPassword789!\"
  }"
```

### 7. Logout (Revoke Token)

```bash
curl -X POST http://localhost:8080/api/auth/revoke \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

## Organization Tests

### 1. List Organizations

```bash
curl -X GET http://localhost:8080/api/orgs \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 2. Create Organization

```bash
curl -X POST http://localhost:8080/api/orgs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "name": "New Branch Office",
    "slug": "branch-office"
  }'
```

### 3. Switch Organization

```bash
# Get target org ID from list
TARGET_ORG_ID="<org-uuid>"

curl -X POST http://localhost:8080/api/auth/switch-org \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{
    \"organizationId\": \"$TARGET_ORG_ID\"
  }"
```

## Workspace Tests

### 1. List Workspaces

```bash
curl -X GET http://localhost:8080/api/workspaces \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 2. Create Workspace

```bash
curl -X POST http://localhost:8080/api/workspaces \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "name": "Project Workspace",
    "description": "Workspace for project documentation",
    "isPrivate": false
  }'
```

### 3. Get Workspace

```bash
WORKSPACE_ID="<workspace-uuid>"

curl -X GET http://localhost:8080/api/workspaces/$WORKSPACE_ID \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 4. Update Workspace

```bash
curl -X PUT http://localhost:8080/api/workspaces/$WORKSPACE_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "name": "Updated Workspace Name",
    "description": "Updated description"
  }'
```

### 5. Delete Workspace

```bash
curl -X DELETE http://localhost:8080/api/workspaces/$WORKSPACE_ID \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

## Page Tests

### 1. List Pages in Workspace

```bash
curl -X GET http://localhost:8080/api/workspaces/$WORKSPACE_ID/pages \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 2. Create Page

```bash
curl -X POST http://localhost:8080/api/workspaces/$WORKSPACE_ID/pages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "title": "Meeting Notes",
    "emoji": "📝"
  }'
```

### 3. Get Page with Blocks

```bash
PAGE_ID="<page-uuid>"

curl -X GET http://localhost:8080/api/pages/$PAGE_ID \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 4. Update Page

```bash
curl -X PUT http://localhost:8080/api/pages/$PAGE_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "title": "Updated Meeting Notes",
    "emoji": "✏️"
  }'
```

## Error Scenarios

### 1. Invalid Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@acme.com",
    "password": "WrongPassword"
  }'
```

**Expected: 400 Bad Request**

### 2. Expired Access Token

```bash
# Use old/expired token
curl -X GET http://localhost:8080/api/workspaces \
  -H "Authorization: Bearer <expired-token>"
```

**Expected: 401 Unauthorized**

### 3. Invalid Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "invalid-token"
  }'
```

**Expected: 400 Bad Request - "Invalid or expired refresh token"**

### 4. Unauthorized Resource Access

```bash
# Try to access another user's private workspace
curl -X GET http://localhost:8080/api/workspaces/<other-user-workspace> \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Expected: 403 Forbidden**

## Swagger UI Testing

1. Open: `http://localhost:8080/q/swagger-ui`
2. Click **Authorize** button (top right)
3. Login to get access token
4. Paste token without "Bearer" prefix
5. Click **Authorize**
6. Test endpoints directly in UI

## Postman Collection

Import the Postman collection for easier testing:

1. File → Import
2. Select `postman-collection.json`
3. Configure environment variables:
   - `base_url`: `http://localhost:8080`
   - `access_token`: (auto-populated after login)
   - `refresh_token`: (auto-populated after login)

## Test Data

Default test users (see `V9__uuid_test_data.sql`):

| Email | Password | Organization | Role |
|-------|----------|--------------|------|
| john@acme.com | Password123! | Acme Corporation | OWNER |
| jane@acme.com | Password123! | Acme Corporation | ADMIN |
| bob@acme.com | Password123! | Acme Corporation | MEMBER |

## Performance Testing

Use Apache Bench for load testing:

```bash
# Test login endpoint
ab -n 1000 -c 10 -p login.json -T application/json \
  http://localhost:8080/api/auth/login
```

Where `login.json` contains:
```json
{"email":"john@acme.com","password":"Password123!"}
```

## CI/CD Testing

GitHub Actions workflow example:

```yaml
- name: Run integration tests
  run: |
    ./gradlew test
    curl -f http://localhost:8080/q/health || exit 1
```
