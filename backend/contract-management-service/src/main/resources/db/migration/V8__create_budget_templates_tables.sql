-- V8__create_budget_templates_tables.sql

-- Table for Budget Templates (master list)
CREATE TABLE budget_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Table for Contract Budget Templates (junction table)
CREATE TABLE contract_budget_templates (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
    budget_template_id BIGINT NOT NULL REFERENCES budget_templates(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE(contract_id, budget_template_id)
);

-- Indexes
CREATE INDEX idx_budget_templates_name ON budget_templates(name);
CREATE INDEX idx_contract_budget_template_contract ON contract_budget_templates(contract_id);
CREATE INDEX idx_contract_budget_template_template ON contract_budget_templates(budget_template_id);

-- Insert default budget templates
INSERT INTO budget_templates (name, description, created_by) VALUES
    ('Personnel Services', 'Personnel Services budget category', 'system'),
    ('Operations and Support', 'Operations and Support budget category', 'system'),
    ('Utilities', 'Utilities budget category', 'system'),
    ('Professional Services', 'Professional Services budget category', 'system'),
    ('Rent', 'Rent budget category', 'system'),
    ('Contracted Services', 'Contracted Services budget category', 'system'),
    ('Rate', 'Rate budget category', 'system'),
    ('Milestone', 'Milestone budget category', 'system'),
    ('Unallocated Funds', 'Unallocated Funds budget category', 'system'),
    ('Indirect Rate', 'Indirect Rate budget category', 'system'),
    ('Program Income', 'Program Income budget category', 'system');

-- Add comments for documentation
COMMENT ON TABLE budget_templates IS 'Master list of available budget templates/categories.';
COMMENT ON TABLE contract_budget_templates IS 'Junction table linking contracts to selected budget templates.';

