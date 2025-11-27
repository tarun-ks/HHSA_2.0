import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { workflowService } from '../../../services/workflowService';
import { getWorkflowByProcessKey } from '../workflowDefinitions';
import { Loader } from '../../../components/atoms/Loader';
import { Button } from '../../../components/atoms/Button';
import { useNavigate } from 'react-router-dom';
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
  const navigate = useNavigate();
  const [showDiagram, setShowDiagram] = useState(false);
  const [showHistory, setShowHistory] = useState(false);

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
    <div className="space-y-3">
      <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded">
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-1">
            <span className="text-sm font-medium text-gray-900 dark:text-white">
              {workflow?.displayName || workflowId}
            </span>
            <span className={`px-2 py-1 text-xs font-semibold rounded-full ${statusColor}`}>
              {processInstance.state}
            </span>
          </div>
          <div className="text-xs text-gray-500 dark:text-gray-400">
            Instance: {instanceKey} • Started: {processInstance.startTime ? new Date(processInstance.startTime).toLocaleString() : 'N/A'}
          </div>
        </div>
        <div className="flex space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowDiagram(!showDiagram)}
          >
            {showDiagram ? 'Hide Diagram' : 'View Diagram'}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowHistory(!showHistory)}
          >
            {showHistory ? 'Hide History' : 'View History'}
          </Button>
        </div>
      </div>
      
      {showDiagram && (
        <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 bg-white dark:bg-gray-800">
          <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-3">
            Workflow Diagram: {workflow?.displayName || workflowId}
          </h4>
          <BpmnDiagramViewer
            workflowId={workflowId}
            processInstanceKey={instanceKey}
            height="500px"
          />
        </div>
      )}

      {showHistory && (
        <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 bg-white dark:bg-gray-800">
          <TaskHistoryTimeline processInstanceKey={instanceKey} />
        </div>
      )}
    </div>
  );
};

export default ProcessInstanceStatus;

