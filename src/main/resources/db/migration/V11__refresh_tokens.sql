-- V11: Create refresh_tokens table for JWT refresh token functionality
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_used_at TIMESTAMP
);

-- Index for fast token lookups
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- Index for user lookups
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Index for cleanup of expired/revoked tokens
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked_at ON refresh_tokens(revoked_at);
