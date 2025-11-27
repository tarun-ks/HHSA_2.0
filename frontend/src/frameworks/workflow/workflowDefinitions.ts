/**
 * Workflow definitions configuration.
 * Centralized configuration for all workflows in the system.
 * To add a new workflow, simply add a new entry to WORKFLOW_DEFINITIONS.
 */

export interface WorkflowDefinition {
  processKey: string;        // BPMN process ID (e.g., "ContractConfiguration")
  workflowId: string;        // Workflow ID (e.g., "WF302")
  displayName: string;       // Display name (e.g., "Contract Configuration")
  description: string;       // Description
  requiredVariables: string[]; // Required process variables
}

/**
 * Registry of all workflow definitions.
 * Add new workflows here - no code changes needed elsewhere.
 */
export const WORKFLOW_DEFINITIONS: Record<string, WorkflowDefinition> = {
  WF302: {
    processKey: "ContractConfiguration",
    workflowId: "WF302",
    displayName: "Contract Configuration",
    description: "Workflow for contract Chart of Accounts (COA) configuration",
    requiredVariables: ["contractId", "initiator"],
  },
  WF303: {
    processKey: "ContractCOF",
    workflowId: "WF303",
    displayName: "Contract Certification of Funds",
    description: "Workflow for contract Certification of Funds (COF) approval",
    requiredVariables: ["contractId", "initiator"],
  },
};

/**
 * Get workflow definition by process key (BPMN process ID)
 */
export function getWorkflowByProcessKey(processKey: string): WorkflowDefinition | undefined {
  return Object.values(WORKFLOW_DEFINITIONS).find(w => w.processKey === processKey);
}

/**
 * Get workflow definition by workflow ID (e.g., "WF302")
 */
export function getWorkflowById(workflowId: string): WorkflowDefinition | undefined {
  return WORKFLOW_DEFINITIONS[workflowId];
}

/**
 * Get all workflow definitions
 */
export function getAllWorkflows(): WorkflowDefinition[] {
  return Object.values(WORKFLOW_DEFINITIONS);
}

/**
 * Check if a workflow exists
 */
export function hasWorkflow(workflowId: string): boolean {
  return workflowId in WORKFLOW_DEFINITIONS;
}

