-- V1__create_documents_table.sql

-- Table for Documents
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    storage_key VARCHAR(500) NOT NULL UNIQUE,
    storage_provider VARCHAR(50),
    entity_type VARCHAR(100),
    entity_id BIGINT,
    description VARCHAR(500),
    category VARCHAR(50),
    uploaded_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_documents_storage_key ON documents(storage_key);
CREATE INDEX idx_documents_entity_type ON documents(entity_type, entity_id);
CREATE INDEX idx_documents_category ON documents(category);
CREATE INDEX idx_documents_uploaded_by ON documents(uploaded_by);


