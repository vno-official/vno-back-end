-- V5: Create sequences for Hibernate Reactive
-- Hibernate Reactive uses sequences for ID generation, which must be created explicitly

-- Sequences for all entities using IDENTITY generation
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS organizations_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS user_organizations_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS invitations_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS workspaces_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS pages_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS blocks_seq START WITH 1 INCREMENT BY 50;
