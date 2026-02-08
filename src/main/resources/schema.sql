CREATE TABLE IF NOT EXISTS link (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(10) UNIQUE,
    original_url TEXT NOT NULL,
    author       VARCHAR(255),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS click (
    id         BIGSERIAL PRIMARY KEY,
    link_id    BIGINT NOT NULL REFERENCES link(id),
    clicked_at TIMESTAMP NOT NULL DEFAULT NOW(),
    referrer   VARCHAR(2048),
    user_agent VARCHAR(1024)
);

CREATE INDEX IF NOT EXISTS idx_click_link_id ON click (link_id);
