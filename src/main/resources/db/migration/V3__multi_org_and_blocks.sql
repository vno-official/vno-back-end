-- Phase 1 MVP: Multi-org support and Block editor
-- Migration V3: Add user_organizations, blocks table, and workspace system flag

-- 1. Create user_organizations junction table (replaces memberships for multi-org support)
CREATE TABLE user_organizations (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    organization_id     BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    role                TEXT NOT NULL CHECK (role IN ('OWNER','ADMIN','MEMBER')),
    joined_at           TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, organization_id)
);

CREATE INDEX idx_user_orgs_user ON user_organizations(user_id);
CREATE INDEX idx_user_orgs_org ON user_organizations(organization_id);

-- 2. Migrate existing memberships data to user_organizations
INSERT INTO user_organizations (user_id, organization_id, role, joined_at)
SELECT user_id, organization_id, role, joined_at
FROM memberships
ON CONFLICT (user_id, organization_id) DO NOTHING;

-- 3. Add is_system flag to workspaces (for Private workspace)
ALTER TABLE workspaces ADD COLUMN is_system BOOLEAN DEFAULT false;

-- Mark any existing workspace named "Private" as system workspace
UPDATE workspaces SET is_system = true WHERE name = 'Private';

-- 4. Create blocks table for editor content
CREATE TABLE blocks (
    id                  BIGSERIAL PRIMARY KEY,
    organization_id     BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    page_id             BIGINT NOT NULL REFERENCES pages(id) ON DELETE CASCADE,
    parent_block_id     BIGINT REFERENCES blocks(id) ON DELETE SET NULL,
    type                TEXT NOT NULL,  -- text, heading_1, heading_2, heading_3, todo, toggle, bullet_list, numbered_list, callout, divider, code
    content             JSONB NOT NULL DEFAULT '{}',
    order_index         INTEGER NOT NULL,
    created_by          BIGINT REFERENCES users(id),
    created_at          TIMESTAMPTZ DEFAULT now(),
    updated_at          TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_blocks_org ON blocks(organization_id);
CREATE INDEX idx_blocks_page ON blocks(page_id);
CREATE INDEX idx_blocks_order ON blocks(page_id, order_index);

-- 5. Add updated_at trigger for blocks
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_blocks_updated_at BEFORE UPDATE ON blocks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_pages_updated_at BEFORE UPDATE ON pages
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
