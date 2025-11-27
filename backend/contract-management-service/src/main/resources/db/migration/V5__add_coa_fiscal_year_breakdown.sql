-- V5__add_coa_fiscal_year_breakdown.sql

-- Table for COA Fiscal Year Breakdown
-- Links to contract_configurations and stores amount per fiscal year
CREATE TABLE contract_coa_fiscal_years (
    id BIGSERIAL PRIMARY KEY,
    contract_configuration_id BIGINT NOT NULL REFERENCES contract_configurations(id) ON DELETE CASCADE,
    fiscal_year VARCHAR(10) NOT NULL, -- e.g., "FY12", "FY13", "FY14", "FY15", "FY16"
    amount NUMERIC(19, 2) NOT NULL CHECK (amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(contract_configuration_id, fiscal_year)
);

-- Indexes for COA Fiscal Year Breakdown
CREATE INDEX idx_coa_fiscal_year_config ON contract_coa_fiscal_years(contract_configuration_id);
CREATE INDEX idx_coa_fiscal_year_year ON contract_coa_fiscal_years(fiscal_year);

-- Add comment for documentation
COMMENT ON TABLE contract_coa_fiscal_years IS 'Stores fiscal year breakdown for each COA allocation. Each contract_configuration can have multiple fiscal year entries (FY12-FY16).';

