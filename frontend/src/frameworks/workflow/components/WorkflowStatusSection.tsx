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
      <div className="text-center py-8">
        <p className="text-sm text-gray-500 dark:text-gray-400">
          No active workflows
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
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
  );
};
