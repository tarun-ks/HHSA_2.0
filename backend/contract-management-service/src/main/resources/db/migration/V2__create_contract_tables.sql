-- V1__create_contract_tables.sql

-- Table for Contracts
CREATE TABLE contracts (
    id BIGSERIAL PRIMARY KEY,
    contract_number VARCHAR(30) NOT NULL UNIQUE,
    contract_title VARCHAR(255) NOT NULL,
    contract_value NUMERIC(19, 2) NOT NULL,
    contract_amount NUMERIC(19, 2) NOT NULL,
    contract_start_date DATE NOT NULL,
    contract_end_date DATE NOT NULL,
    status_id INTEGER NOT NULL CHECK (status_id BETWEEN 59 AND 69),
    agency_id VARCHAR(50) NOT NULL,
    program_id VARCHAR(50),
    provider_id VARCHAR(50) NOT NULL,
    organization_id VARCHAR(50) NOT NULL,
    e_pin VARCHAR(50) NOT NULL,
    registration_flag VARCHAR(1),
    procurement_id VARCHAR(50),
    parent_contract_id BIGINT,
    contract_type_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for Contracts
CREATE INDEX idx_contracts_number ON contracts(contract_number);
CREATE INDEX idx_contracts_status ON contracts(status_id);
CREATE INDEX idx_contracts_agency ON contracts(agency_id);
CREATE INDEX idx_contracts_provider ON contracts(provider_id);

-- Table for Contract Configurations (Chart of Accounts)
CREATE TABLE contract_configurations (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id),
    uobc VARCHAR(50) NOT NULL,
    sub_oc VARCHAR(50) NOT NULL,
    rc VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(contract_id, uobc, sub_oc, rc)
);

-- Indexes for Contract Configurations
CREATE INDEX idx_contract_config_contract ON contract_configurations(contract_id);
CREATE INDEX idx_contract_config_coa ON contract_configurations(uobc, sub_oc, rc);

-- Table for Contract Funding Sources
CREATE TABLE contract_funding_sources (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id),
    funding_source_id VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for Contract Funding Sources
CREATE INDEX idx_contract_funding_contract ON contract_funding_sources(contract_id);


