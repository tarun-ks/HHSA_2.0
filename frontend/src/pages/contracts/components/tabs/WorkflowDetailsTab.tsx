import { useQuery } from '@tanstack/react-query';
import { contractService } from '../../../../services/contractService';
import { Loader } from '../../../../components/atoms/Loader';
import { TaskDetailsSection } from '../TaskDetailsSection';
import { WorkflowStatusSection } from '../../../../frameworks/workflow/components/WorkflowStatusSection';
import { useAppSelector } from '../../../../store/hooks';

interface WorkflowDetailsTabProps {
  contractId: number;
}

/**
 * Workflow Details Tab
 * Displays workflow status, tasks, and history
 */
export const WorkflowDetailsTab = ({ contractId }: WorkflowDetailsTabProps) => {
  const user = useAppSelector((state) => state.auth.user);

  const { data, isLoading, error } = useQuery({
    queryKey: ['contract', contractId],
    queryFn: () => contractService.getContract(contractId),
    enabled: !!contractId,
  });

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <Loader size="md" />
      </div>
    );
  }

  if (error || !data?.data) {
    return (
      <div className="text-center py-12 text-red-600 dark:text-red-400">
        Failed to load workflow information
      </div>
    );
  }

  const contract = data.data.contract;

  return (
    <div className="space-y-6">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
        Workflow Details
      </h3>

      {/* Task Details Section */}
      <div>
        <h4 className="text-md font-medium text-gray-900 dark:text-white mb-3">
          Active Tasks
        </h4>
        <TaskDetailsSection
          contractId={contractId}
          processInstanceKey={
            contract.configurationWorkflowInstanceKey
              ? parseInt(contract.configurationWorkflowInstanceKey)
              : undefined
          }
          userId={user?.id}
        />
      </div>

      {/* Workflow Status Section */}
      {contract && (contract.configurationWorkflowInstanceKey || contract.cofWorkflowInstanceKey) && (
        <div>
          <h4 className="text-md font-medium text-gray-900 dark:text-white mb-3">
            Workflow Status
          </h4>
          <WorkflowStatusSection
            entityId={contractId}
            entityType="Contract"
            processInstanceKeys={{
              ...(contract.configurationWorkflowInstanceKey && {
                WF302: parseInt(contract.configurationWorkflowInstanceKey),
              }),
              ...(contract.cofWorkflowInstanceKey && {
                WF303: parseInt(contract.cofWorkflowInstanceKey),
              }),
            }}
          />
        </div>
      )}
    </div>
  );
};

