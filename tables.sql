-- 1. Core tenant
organization
    id                  BIGSERIAL PRIMARY KEY
    name                TEXT NOT NULL
    subdomain           TEXT UNIQUE NOT NULL          -- acme.yourapp.com
    plan                TEXT DEFAULT 'free'            -- free | pro | team | enterprise
    stripe_customer_id  TEXT
    trial_ends_at       TIMESTAMPTZ
    created_at          TIMESTAMPTZ DEFAULT now()
    deleted_at          TIMESTAMPTZ                    -- soft delete org (hiếm)

-- 2. User & membership
users
    id                  BIGSERIAL PRIMARY KEY
    email               TEXT UNIQUE NOT NULL
    password_hash       TEXT                           -- chỉ dùng magic link + Google thì có thể NULL
    name                TEXT
    avatar_url          TEXT
    created_at          TIMESTAMPTZ DEFAULT now()

user_organization
    user_id             BIGINT REFERENCES users(id) ON DELETE CASCADE
    organization_id     BIGINT REFERENCES organization(id) ON DELETE CASCADE
    role                TEXT NOT NULL CHECK (role IN ('owner','admin','member','guest'))
    joined_at           TIMESTAMPTZ DEFAULT now()
    PRIMARY KEY (user_id, organization_id)

-- 3. Invite
invitation
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT REFERENCES organization(id)
    email               TEXT NOT NULL
    role                TEXT NOT NULL DEFAULT 'member'
    token               TEXT UNIQUE NOT NULL
    invited_by          BIGINT REFERENCES users(id)
    expires_at          TIMESTAMPTZ NOT NULL
    accepted_at         TIMESTAMPTZ
    created_at          TIMESTAMPTZ DEFAULT now()

-- 4. Workspace & Page tree
workspace
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    name                TEXT NOT NULL
    icon_emoji          TEXT
    cover_url           TEXT
    default_permission TEXT DEFAULT 'organization' -- private | workspace | organization
    created_by          BIGINT REFERENCES users(id)
    created_at          TIMESTAMPTZ DEFAULT now()
    deleted_at          TIMESTAMPTZ

page
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    workspace_id        BIGINT REFERENCES workspace(id) ON DELETE CASCADE
    parent_page_id      BIGINT REFERENCES page(id) ON DELETE SET NULL
    title               TEXT NOT NULL DEFAULT 'Untitled'
    icon_emoji          TEXT
    cover_url           TEXT
    path                LTREE                          -- materialized path: .1.23.456. (siêu nhanh tree query)
    visibility          TEXT DEFAULT 'inherit'         -- inherit | private | workspace | org | public_link
    is_locked           BOOLEAN DEFAULT false
    created_by          BIGINT REFERENCES users(id)
    created_at          TIMESTAMPTZ DEFAULT now()
    updated_at          TIMESTAMPTZ DEFAULT now()
    deleted_at          TIMESTAMPTZ                    -- trash

-- 5. Block (JSONB = tối ưu cho Notion-like)
block
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    page_id             BIGINT REFERENCES page(id) ON DELETE CASCADE
    parent_block_id     BIGINT REFERENCES block(id) ON DELETE SET NULL   -- cho toggle, nested
    type                TEXT NOT NULL                    -- text | heading_1 | todo | toggle | callout | etc.
    content             JSONB NOT NULL DEFAULT '{}'      -- toàn bộ data block
    order_index         INTEGER NOT NULL                 -- để drag-drop nhanh
    created_by          BIGINT REFERENCES users(id)
    created_at          TIMESTAMPTZ DEFAULT now()
    updated_at          TIMESTAMPTZ DEFAULT now()

-- 6. Permission override & Share link
page_share
    id                  BIGSERIAL PRIMARY KEY
    page_id             BIGINT REFERENCES page(id) ON DELETE CASCADE
    share_token         TEXT UNIQUE NOT NULL
    permission          TEXT NOT NULL DEFAULT 'view'     -- view | edit | comment
    password_hash       TEXT                             -- optional
    expires_at          TIMESTAMPTZ
    allow_external      BOOLEAN DEFAULT true
    created_by          BIGINT REFERENCES users(id)
    created_at          TIMESTAMPTZ DEFAULT now()

-- 7. Comment (phase 7)
comment
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    page_id             BIGINT REFERENCES page(id) ON DELETE CASCADE
    block_id            BIGINT REFERENCES block(id) ON DELETE CASCADE
    parent_comment_id   BIGINT REFERENCES comment(id) ON DELETE SET NULL
    author_id           BIGINT REFERENCES users(id)
    content             TEXT NOT NULL
    resolved            BOOLEAN DEFAULT false
    created_at          TIMESTAMPTZ DEFAULT now()
    updated_at          TIMESTAMPTZ DEFAULT now()
    deleted_at          TIMESTAMPTZ

-- 8. Billing & Usage
subscription
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL UNIQUE
    stripe_subscription_id TEXT UNIQUE
    plan                TEXT NOT NULL
    status              TEXT NOT NULL
    current_period_end  TIMESTAMPTZ
    cancel_at_period_end BOOLEAN DEFAULT false
    created_at          TIMESTAMPTZ DEFAULT now()

usage_log
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    date                DATE NOT NULL DEFAULT CURRENT_DATE
    pages_count         INT DEFAULT 0
    blocks_count        INT DEFAULT 0
    storage_bytes       BIGINT DEFAULT 0
    members_count       INT DEFAULT 0
    UNIQUE(organization_id, date)

-- 9. File upload (icon, cover, attachment)
file_upload
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT NOT NULL
    uploaded_by         BIGINT REFERENCES users(id)
    key                 TEXT NOT NULL                    -- path trên R2/S3
    url                 TEXT NOT NULL
    size_bytes          BIGINT NOT NULL
    mime_type           TEXT
    created_at          TIMESTAMPTZ DEFAULT now()

-- 10. Audit log (admin dashboard + debug)
audit_log
    id                  BIGSERIAL PRIMARY KEY
    organization_id     BIGINT
    user_id             BIGINT REFERENCES users(id)
    action              TEXT NOT NULL                    -- login | create_page | invite_member | upgrade_plan
    entity_type         TEXT                             -- page | workspace | organization
    entity_id           BIGINT
    metadata            JSONB DEFAULT '{}'
    ip_address          INET
    user_agent          TEXT
    created_at          TIMESTAMPTZ DEFAULT now()

-- 11. Magic link & password reset
magic_token
    id                  BIGSERIAL PRIMARY KEY
    user_id             BIGINT REFERENCES users(id) ON DELETE CASCADE
    token               TEXT UNIQUE NOT NULL
    expires_at          TIMESTAMPTZ NOT NULL
    used_at             TIMESTAMPTZ
    created_at          TIMESTAMPTZ DEFAULT now()