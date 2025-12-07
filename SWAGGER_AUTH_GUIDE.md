# Swagger Bearer Token Authentication Guide

## Overview
The VNO Backend API now supports Bearer token authentication in Swagger UI. This allows you to test authenticated endpoints directly from the Swagger interface.

## How to Use Bearer Token Authentication

### 1. Access Swagger UI
Navigate to: `http://localhost:8080/q/swagger-ui`

### 2. Obtain a JWT Token
Before you can test protected endpoints, you need to obtain a JWT token. You have two options:

#### Option A: Register a New User
1. Expand the **Authentication** section
2. Find the `POST /api/auth/register` endpoint
3. Click "Try it out"
4. Fill in the request body:
```json
{
  "email": "user@example.com",
  "password": "yourpassword",
  "name": "Your Name"
}
```
5. Click "Execute"
6. Copy the `token` value from the response

#### Option B: Login with Existing User
1. Expand the **Authentication** section
2. Find the `POST /api/auth/login` endpoint
3. Click "Try it out"
4. Fill in the request body:
```json
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```
5. Click "Execute"
6. Copy the `token` value from the response

### 3. Authorize in Swagger UI
1. Click the **"Authorize"** button (lock icon) at the top right of the Swagger UI
2. In the dialog that appears, paste your JWT token in the **Value** field
3. Click **"Authorize"**
4. Click **"Close"**

### 4. Test Protected Endpoints
Now you can test any protected endpoint:
- Organization endpoints (`/api/org`)
- Workspace endpoints (`/api/workspaces`)
- Page endpoints (`/api/pages`)
- Block endpoints (`/api/blocks`)
- Protected auth endpoints (`/api/auth/me`, `/api/auth/switch-org`)

The Bearer token will be automatically included in the `Authorization` header as:
```
Authorization: Bearer <your-jwt-token>
```

### 5. Token Persistence
The Swagger UI is configured to persist your authorization. This means:
- Your token will remain active even if you refresh the page
- You don't need to re-authorize after every page reload
- The token is stored in your browser's local storage

### 6. Logout/Clear Authorization
To clear your token:
1. Click the **"Authorize"** button again
2. Click **"Logout"**
3. Click **"Close"**

## Protected vs Public Endpoints

### Public Endpoints (No Token Required)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with credentials

### Protected Endpoints (Token Required)
All other endpoints require Bearer token authentication:
- Organization management
- Workspace management
- Page management
- Block management
- User profile and organization switching

## Troubleshooting

### "Unauthorized" Error
- Make sure you've clicked "Authorize" and entered your token
- Check that your token hasn't expired (default expiry: 168 hours / 7 days)
- Verify you're using a valid JWT token from login/register

### "Forbidden" Error
- Your token is valid, but you don't have permission for this resource
- Check that you're accessing resources within your organization context
- Try switching to a different organization if you belong to multiple

### Token Expired
- Simply obtain a new token by logging in again
- Update the authorization with the new token

## API Tags
Endpoints are organized into the following tags for easier navigation:
- **Authentication** - Login, register, and user management
- **Organization** - Organization management
- **Workspace** - Workspace CRUD operations
- **Page** - Page management
- **Block** - Editor content blocks

## Additional Notes
- The JWT token contains claims including `user_id`, `org_id`, `role`, and `email`
- Multi-tenant isolation is enforced automatically based on the `org_id` in your token
- Use `POST /api/auth/switch-org` to change your current organization context
