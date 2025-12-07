-- V9: Insert test data with UUID  primary keys
-- Password for all test users: "Password123!" (hashed with BCrypt work factor 12)
-- BCrypt hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye

-- 1. Insert Test Organizations (using specific UUIDs for predictability)
INSERT INTO organizations (id, name, slug, plan, created_at) VALUES
('019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'Acme Corporation', 'acme', 'pro', NOW()),
('019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, 'Tech Startup Inc', 'techstartup', 'free', NOW()),
('019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, 'Design Studio', 'designstudio', 'pro', NOW());

-- 2. Insert Test Users
INSERT INTO users (id, email, password_hash, name, avatar_url, created_at) VALUES
('019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, 'john@acme.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'John Doe', 'https://i.pravatar.cc/150?img=1', NOW()),
('019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, 'jane@techstartup.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Jane Smith', 'https://i.pravatar.cc/150?img=2', NOW()),
('019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, 'bob@designstudio.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Bob Wilson', 'https://i.pravatar.cc/150?img=3', NOW()),
('019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, 'alice@acme.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN96JdO1JlbVHhYvwHWye', 'Alice Johnson', 'https://i.pravatar.cc/150?img=4', NOW());

-- 3. Insert User-Organization Relationships
INSERT INTO user_organizations (id, user_id, organization_id, role, joined_at) VALUES
('019af74d-9ae5-7e11-9b48-2fe784900f11'::uuid, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'OWNER', NOW()),
('019af74d-9ae5-7e11-9b48-2fe784900f12'::uuid, '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'ADMIN', NOW()),
('019af74d-9ae5-7e11-9b48-2fe784900f13'::uuid, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, 'OWNER', NOW()),
('019af74d-9ae5-7e11-9b48-2fe784900f14'::uuid, '019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, 'OWNER', NOW());

-- 4. Insert Workspaces
INSERT INTO workspaces (id, organization_id, name, icon_emoji, default_permission, is_system, created_by, created_at) VALUES
-- Acme workspaces
('019af74e-0549-7a6b-b4ac-bae3f77557d1'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'Private', '🔒', 'private', TRUE, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW()),
('019af74e-0549-7a6b-b4ac-bae3f77557d2'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'Engineering', '⚙️', 'organization', FALSE, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW()),
('019af74e-0549-7a6b-b4ac-bae3f77557d3'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, 'Marketing', '📢', 'organization', FALSE, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW()),
-- Tech Startup workspaces
('019af74e-0549-7a6b-b4ac-bae3f77557d4'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, 'Private', '🔒', 'private', TRUE, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW()),
('019af74e-0549-7a6b-b4ac-bae3f77557d5'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, 'Product', '🚀', 'organization', FALSE, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW()),
-- Design Studio workspaces
('019af74e-0549-7a6b-b4ac-bae3f77557d6'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, 'Private', '🔒', 'private', TRUE, '019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, NOW()),
('019af74e-0549-7a6b-b4ac-bae3f77557d7'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, 'Client Projects', '🎨', 'organization', FALSE, '019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, NOW());

-- 5. Insert Pages
INSERT INTO pages (id, organization_id, workspace_id, parent_page_id, title, icon_emoji, path, visibility, created_by, created_at, updated_at) VALUES
-- Acme Engineering pages
('019af74e-6951-725f-a3bd-21c5f861f6a1'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d2'::uuid, NULL, 'Welcome to Engineering', '👋', '.1.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d2'::uuid, NULL, 'Architecture Docs', '🏗️', '.2.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6a3'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d2'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, 'System Design', '📐', '.2.3.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6a4'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d2'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, 'API Documentation', '📡', '.2.4.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
-- Acme Marketing pages
('019af74e-6951-725f-a3bd-21c5f861f6a5'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d3'::uuid, NULL, 'Marketing Strategy 2024', '📊', '.5.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6a6'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d3'::uuid, NULL, 'Campaign Ideas', '💡', '.6.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, NOW(), NOW()),
-- Tech Startup Product pages
('019af74e-6951-725f-a3bd-21c5f861f6a7'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d5'::uuid, NULL, 'Product Roadmap', '🗺️', '.7.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6a8'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d5'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a7'::uuid, 'Q1 2024 Features', '📅', '.7.8.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW(), NOW()),
-- Design Studio pages
('019af74e-6951-725f-a3bd-21c5f861f6a9'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d7'::uuid, NULL, 'Client: Acme Corp Redesign', '🎨', '.9.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, NOW(), NOW()),
('019af74e-6951-725f-a3bd-21c5f861f6aa'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d633'::uuid, '019af74e-0549-7a6b-b4ac-bae3f77557d7'::uuid, NULL, 'Brand Guidelines', '📚', '.10.', 'inherit', '019af74f-03d9-72a8-b788-5147af2a6ba3'::uuid, NOW(), NOW());

-- 6. Insert Blocks (content for pages)
INSERT INTO blocks (id, organization_id, page_id, type, content, order_index, created_by, created_at, updated_at) VALUES
-- Welcome to Engineering page blocks
('019af74e-9d1f-7e01-b66d-e6d1e774b571'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a1'::uuid, 'HEADING', '{"text":"Welcome to the Engineering Workspace","level":1}', 0, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b572'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a1'::uuid, 'TEXT', '{"text":"This is where we collaborate on technical documentation, architecture decisions, and code reviews."}', 1, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b573'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a1'::uuid, 'BULLET_LIST', '{"items":["Review architecture docs","Check API documentation","Join daily standups"]}', 2, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),

-- Architecture Docs page blocks
('019af74e-9d1f-7e01-b66d-e6d1e774b574'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, 'HEADING', '{"text":"System Architecture","level":1}', 0, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b575'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, 'TEXT', '{" text":"Our system follows a microservices architecture with the following components:"}', 1, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b576'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a2'::uuid, 'CODE', '{"language":"yaml","code":"services:\n  - api-gateway\n  - auth-service\n  - user-service\n  - workspace-service"}', 2, '019af74f-03d9-72a8-b788-5147af2a6ba1'::uuid, NOW(), NOW()),

-- Product Roadmap page blocks
('019af74e-9d1f-7e01-b66d-e6d1e774b577'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a7'::uuid, 'HEADING', '{"text":"Product Roadmap 2024","level":1}', 0, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b578'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a7'::uuid, 'TEXT', '{"text":"Our vision for the product this year"}', 1, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b579'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d632'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a7'::uuid, 'TODO_LIST', '{"items":[{"text":"Launch mobile app","checked":false},{"text":"Implement real-time collaboration","checked":false},{"text":"Add AI features","checked":false}]}', 2, '019af74f-03d9-72a8-b788-5147af2a6ba2'::uuid, NOW(), NOW()),

-- Marketing Strategy page blocks
('019af74e-9d1f-7e01-b66d-e6d1e774b57a'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a5'::uuid, 'HEADING', '{"text":"Marketing Strategy 2024","level":1}', 0, '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b57b'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a5'::uuid, 'TEXT', '{"text":"Key initiatives for growing our customer base"}', 1, '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, NOW(), NOW()),
('019af74e-9d1f-7e01-b66d-e6d1e774b57c'::uuid, '019af74e-c5d6-7fa2-ab51-b942d989d631'::uuid, '019af74e-6951-725f-a3bd-21c5f861f6a5'::uuid, 'CALLOUT', '{"type":"info","text":"Focus on enterprise customers in Q1"}', 2, '019af74f-03d9-72a8-b788-5147af2a6ba4'::uuid, NOW(), NOW());
