-- V3__add_workflow_fields.sql

-- Add workflow instance keys to contracts table (idempotent)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contracts' AND column_name = 'configuration_workflow_instance_key') THEN
        ALTER TABLE contracts ADD COLUMN configuration_workflow_instance_key VARCHAR(255);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contracts' AND column_name = 'cof_workflow_instance_key') THEN
        ALTER TABLE contracts ADD COLUMN cof_workflow_instance_key VARCHAR(255);
    END IF;
END $$;

-- Index for workflow instance keys (idempotent)
CREATE INDEX IF NOT EXISTS idx_contracts_config_workflow ON contracts(configuration_workflow_instance_key);
CREATE INDEX IF NOT EXISTS idx_contracts_cof_workflow ON contracts(cof_workflow_instance_key);


