import { useQuery } from '@tanstack/react-query';
import { workflowService } from '../../../services/workflowService';
import { getWorkflowByProcessKey } from '../workflowDefinitions';
import { Loader } from '../../../components/atoms/Loader';
import { Card } from '../../../components/atoms/Card';
import { BpmnDiagramViewer } from './BpmnDiagramViewer';
import { TaskHistoryTimeline } from './TaskHistoryTimeline';

interface ProcessInstanceStatusProps {
  workflowId: string;
  instanceKey: number;
  entityId: number;
  entityType: string;
}

/**
 * Individual process instance status component.
 * Used by WorkflowStatusSection to avoid hook rules violations.
 */
const ProcessInstanceStatus = ({ workflowId, instanceKey, entityId, entityType }: ProcessInstanceStatusProps) => {

  const { data, isLoading } = useQuery({
    queryKey: ['process-instance', instanceKey],
    queryFn: () => workflowService.getProcessInstance(instanceKey),
    enabled: !!instanceKey,
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded">
        <div className="flex items-center space-x-2">
          <Loader size="sm" />
          <span className="text-sm text-gray-500 dark:text-gray-400">Loading...</span>
        </div>
      </div>
    );
  }

  if (!data?.data) {
    return null;
  }

  const processInstance = data.data;
  const workflow = getWorkflowByProcessKey(processInstance.bpmnProcessId || '');

  const statusColor = {
    ACTIVE: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300',
    COMPLETED: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300',
    CANCELED: 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-300',
    TERMINATED: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300',
  }[processInstance.state] || 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';

  return (
    <div className="space-y-4">
      <Card>
        <div className="flex items-center flex-wrap gap-2 mb-2">
          <h4 className="text-base font-semibold text-gray-900 dark:text-white">
            {workflow?.displayName || workflowId}
          </h4>
          <span className={`badge ${statusColor}`}>
            {processInstance.state}
          </span>
        </div>
        <div className="text-xs text-gray-500 dark:text-gray-400 space-y-1">
          <p>Instance: <span className="font-medium text-gray-700 dark:text-gray-300">{instanceKey}</span></p>
          <p>Started: <span className="font-medium text-gray-700 dark:text-gray-300">
            {processInstance.startTime ? new Date(processInstance.startTime).toLocaleString() : 'N/A'}
          </span></p>
        </div>
      </Card>
      
      <Card>
        <div className="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
          <h4 className="text-base font-semibold text-gray-900 dark:text-white">
            Workflow Diagram
          </h4>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {workflow?.displayName || workflowId} - Visual representation with status indicators
          </p>
        </div>
        <div className="overflow-hidden rounded-lg border border-gray-200 dark:border-gray-700">
          <BpmnDiagramViewer
            workflowId={workflowId}
            processInstanceKey={instanceKey}
            height="500px"
          />
        </div>
      </Card>

      <Card>
        <TaskHistoryTimeline processInstanceKey={instanceKey} />
      </Card>
    </div>
  );
};

export default ProcessInstanceStatus;

