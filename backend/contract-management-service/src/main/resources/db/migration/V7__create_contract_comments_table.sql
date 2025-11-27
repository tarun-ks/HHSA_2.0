-- V7__create_contract_comments_table.sql

-- Table for Contract Comments
CREATE TABLE contract_comments (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
    task_id VARCHAR(100), -- Optional: Link to workflow task
    comment_text TEXT NOT NULL,
    author_id VARCHAR(100) NOT NULL,
    author_name VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for Contract Comments
CREATE INDEX idx_contract_comments_contract ON contract_comments(contract_id);
CREATE INDEX idx_contract_comments_task ON contract_comments(task_id);
CREATE INDEX idx_contract_comments_created ON contract_comments(created_at DESC);

-- Add comment for documentation
COMMENT ON TABLE contract_comments IS 'Stores comments related to contracts and workflow tasks.';

