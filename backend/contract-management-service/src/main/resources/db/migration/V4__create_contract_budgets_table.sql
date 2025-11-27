-- V4__create_contract_budgets_table.sql

-- Table for Contract Budgets
CREATE TABLE contract_budgets (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id),
    fiscal_year VARCHAR(10) NOT NULL,
    budget_code VARCHAR(50) NOT NULL,
    object_code VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for Contract Budgets
CREATE INDEX idx_contract_budget_contract ON contract_budgets(contract_id);
CREATE INDEX idx_contract_budget_fiscal_year ON contract_budgets(contract_id, fiscal_year);

