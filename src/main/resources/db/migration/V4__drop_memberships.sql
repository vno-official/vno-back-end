-- V4: Drop memberships table (data already migrated to user_organizations in V3)

DROP TABLE IF EXISTS memberships CASCADE;
