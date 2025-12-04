-- V6: Insert test data for development and testing
-- Password for all test users: "Password123!" (hashed with BCrypt work factor 12)
-- BCrypt hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye

-- 1. Insert Test Organizations
INSERT INTO organizations (id, name, slug, plan, created_at) VALUES
(1, 'Acme Corporation', 'acme', 'pro', NOW()),
(2, 'Tech Startup Inc', 'techstartup', 'free', NOW()),
(3, 'Design Studio', 'designstudio', 'pro', NOW());

-- 2. Insert Test Users
INSERT INTO users (id, email, password_hash, name, avatar_url, created_at) VALUES
(1, 'john@acme.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'John Doe', 'https://i.pravatar.cc/150?img=1', NOW()),
(2, 'jane@techstartup.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Jane Smith', 'https://i.pravatar.cc/150?img=2', NOW()),
(3, 'bob@designstudio.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Bob Wilson', 'https://i.pravatar.cc/150?img=3', NOW()),
(4, 'alice@acme.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Alice Johnson', 'https://i.pravatar.cc/150?img=4', NOW());

-- 3. Insert User-Organization Relationships
INSERT INTO user_organizations (id, user_id, organization_id, role, joined_at) VALUES
(1, 1, 1, 'OWNER', NOW()),     -- John is owner of Acme
(2, 4, 1, 'ADMIN', NOW()),     -- Alice is admin at Acme
(3, 2, 2, 'OWNER', NOW()),     -- Jane is owner of Tech Startup
(4, 3, 3, 'OWNER', NOW());     -- Bob is owner of Design Studio

-- 4. Insert Workspaces
INSERT INTO workspaces (id, organization_id, name, icon_emoji, default_permission, is_system, created_by, created_at) VALUES
-- Acme workspaces
(1, 1, 'Private', '🔒', 'private', TRUE, 1, NOW()),
(2, 1, 'Engineering', '⚙️', 'organization', FALSE, 1, NOW()),
(3, 1, 'Marketing', '📢', 'organization', FALSE, 1, NOW()),
-- Tech Startup workspaces
(4, 2, 'Private', '🔒', 'private', TRUE, 2, NOW()),
(5, 2, 'Product', '🚀', 'organization', FALSE, 2, NOW()),
-- Design Studio workspaces
(6, 3, 'Private', '🔒', 'private', TRUE, 3, NOW()),
(7, 3, 'Client Projects', '🎨', 'organization', FALSE, 3, NOW());

-- 5. Insert Pages
INSERT INTO pages (id, organization_id, workspace_id, parent_page_id, title, icon_emoji, path, visibility, created_by, created_at, updated_at) VALUES
-- Acme Engineering pages
(1, 1, 2, NULL, 'Welcome to Engineering', '👋', '.1.', 'inherit', 1, NOW(), NOW()),
(2, 1, 2, NULL, 'Architecture Docs', '🏗️', '.2.', 'inherit', 1, NOW(), NOW()),
(3, 1, 2, 2, 'System Design', '📐', '.2.3.', 'inherit', 1, NOW(), NOW()),
(4, 1, 2, 2, 'API Documentation', '📡', '.2.4.', 'inherit', 1, NOW(), NOW()),
-- Acme Marketing pages
(5, 1, 3, NULL, 'Marketing Strategy 2024', '📊', '.5.', 'inherit', 4, NOW(), NOW()),
(6, 1, 3, NULL, 'Campaign Ideas', '💡', '.6.', 'inherit', 4, NOW(), NOW()),
-- Tech Startup Product pages
(7, 2, 5, NULL, 'Product Roadmap', '🗺️', '.7.', 'inherit', 2, NOW(), NOW()),
(8, 2, 5, 7, 'Q1 2024 Features', '📅', '.7.8.', 'inherit', 2, NOW(), NOW()),
-- Design Studio pages
(9, 3, 7, NULL, 'Client: Acme Corp Redesign', '🎨', '.9.', 'inherit', 3, NOW(), NOW()),
(10, 3, 7, NULL, 'Brand Guidelines', '📚', '.10.', 'inherit', 3, NOW(), NOW());

-- 6. Insert Blocks (content for pages)
INSERT INTO blocks (id, organization_id, page_id, type, content, order_index, created_by, created_at, updated_at) VALUES
-- Welcome to Engineering page blocks
(1, 1, 1, 'HEADING', '{"text":"Welcome to the Engineering Workspace","level":1}', 0, 1, NOW(), NOW()),
(2, 1, 1, 'TEXT', '{"text":"This is where we collaborate on technical documentation, architecture decisions, and code reviews."}', 1, 1, NOW(), NOW()),
(3, 1, 1, 'BULLET_LIST', '{"items":["Review architecture docs","Check API documentation","Join daily standups"]}', 2, 1, NOW(), NOW()),

-- Architecture Docs page blocks
(4, 1, 2, 'HEADING', '{"text":"System Architecture","level":1}', 0, 1, NOW(), NOW()),
(5, 1, 2, 'TEXT', '{"text":"Our system follows a microservices architecture with the following components:"}', 1, 1, NOW(), NOW()),
(6, 1, 2, 'CODE', '{"language":"yaml","code":"services:\n  - api-gateway\n  - auth-service\n  - user-service\n  - workspace-service"}', 2, 1, NOW(), NOW()),

-- Product Roadmap page blocks
(7, 2, 7, 'HEADING', '{"text":"Product Roadmap 2024","level":1}', 0, 2, NOW(), NOW()),
(8, 2, 7, 'TEXT', '{"text":"Our vision for the product this year"}', 1, 2, NOW(), NOW()),
(9, 2, 7, 'TODO_LIST', '{"items":[{"text":"Launch mobile app","checked":false},{"text":"Implement real-time collaboration","checked":false},{"text":"Add AI features","checked":false}]}', 2, 2, NOW(), NOW()),

-- Marketing Strategy page blocks
(10, 1, 5, 'HEADING', '{"text":"Marketing Strategy 2024","level":1}', 0, 4, NOW(), NOW()),
(11, 1, 5, 'TEXT', '{"text":"Key initiatives for growing our customer base"}', 1, 4, NOW(), NOW()),
(12, 1, 5, 'CALLOUT', '{"type":"info","text":"Focus on enterprise customers in Q1"}', 2, 4, NOW(), NOW());

-- Update sequences to match inserted IDs
SELECT setval('organizations_seq', 10, true);
SELECT setval('users_seq', 10, true);
SELECT setval('user_organizations_seq', 10, true);
SELECT setval('workspaces_seq', 10, true);
SELECT setval('pages_seq', 15, true);
SELECT setval('blocks_seq', 20, true);
