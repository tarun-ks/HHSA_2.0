import { useQuery } from '@tanstack/react-query';
import { contractService } from '../../../../services/contractService';
import { Loader } from '../../../../components/atoms/Loader';
import { Card } from '../../../../components/atoms/Card';
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
    <div className="section-spacing">
      {/* Task Details Section */}
      <Card>
        <div className="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
            Active Tasks
          </h3>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            View and manage active workflow tasks for this contract
          </p>
        </div>
        <TaskDetailsSection
          contractId={contractId}
          processInstanceKey={
            contract.configurationWorkflowInstanceKey
              ? parseInt(contract.configurationWorkflowInstanceKey)
              : undefined
          }
          userId={user?.id}
        />
      </Card>

      {/* Workflow Status Section */}
      {contract && (contract.configurationWorkflowInstanceKey || contract.cofWorkflowInstanceKey) && (
        <Card>
          <div className="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
              Workflow Status
            </h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              Monitor workflow progress with visual diagrams and history
            </p>
          </div>
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
        </Card>
      )}
    </div>
  );
};

