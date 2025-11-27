import { useMemo } from 'react';
import ProcessInstanceStatus from './ProcessInstanceStatus';

interface WorkflowStatusSectionProps {
  entityId: number;
  entityType: string;
  processInstanceKeys?: {
    [workflowId: string]: number; // { WF302: 12345, WF303: 67890 }
  };
}

/**
 * Reusable component to display workflow status for any entity.
 * Works for Contracts, Procurement, or any entity with workflows.
 */
export const WorkflowStatusSection = ({
  entityId,
  entityType,
  processInstanceKeys = {},
}: WorkflowStatusSectionProps) => {
  // Get all process instance entries
  const entries = useMemo(
    () => Object.entries(processInstanceKeys).filter(([_, key]) => !!key),
    [processInstanceKeys]
  );

  if (entries.length === 0) {
    return (
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white mb-2">
          Workflow Status
        </h3>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          No active workflows
        </p>
      </div>
    );
  }

  return (
    <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 space-y-4">
      <h3 className="text-md font-semibold text-gray-900 dark:text-white">
        Workflow Status
      </h3>

      <div className="space-y-3">
        {entries.map(([workflowId, instanceKey]) => (
          <ProcessInstanceStatus
            key={workflowId}
            workflowId={workflowId}
            instanceKey={instanceKey}
            entityId={entityId}
            entityType={entityType}
          />
        ))}
      </div>
    </div>
  );
};
