import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import { workflowService } from '../../services/workflowService';
import { Loader } from '../../components/atoms/Loader';
import { useToast } from '../../hooks/useToast';

/**
 * Workflow history page.
 * Displays process instance history for a contract.
 */
export const WorkflowHistoryPage = () => {
  const { contractId } = useParams<{ contractId: string }>();
  const toast = useToast();

  const { data, isLoading, error } = useQuery({
    queryKey: ['workflow-history', contractId],
    queryFn: () => workflowService.getProcessInstanceHistory(Number(contractId)),
    enabled: !!contractId,
  });

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="lg" />
      </div>
    );
  }

  if (error) {
    toast.error('Failed to load workflow history');
    return null;
  }

  const history = data?.data;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Workflow History</h1>

      {history && (
        <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6">
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">Process Instance</p>
              <p className="text-base font-medium text-gray-900 dark:text-white">
                {history.processInstanceKey}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">Status</p>
              <span className="inline-block px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
                {history.state}
              </span>
            </div>
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">Started</p>
              <p className="text-base font-medium text-gray-900 dark:text-white">
                {history.startTime ? new Date(history.startTime).toLocaleString() : 'N/A'}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">Ended</p>
              <p className="text-base font-medium text-gray-900 dark:text-white">
                {history.endTime ? new Date(history.endTime).toLocaleString() : 'Active'}
              </p>
            </div>
          </div>

          {history.activities && history.activities.length > 0 && (
            <div>
              <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Activity History
              </h2>
              <div className="space-y-2">
                {history.activities.map((activity: any) => (
                  <div
                    key={activity.activityInstanceKey}
                    className="border-l-4 border-blue-500 pl-4 py-2"
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-medium text-gray-900 dark:text-white">
                          {activity.activityName || activity.activityId}
                        </p>
                        <p className="text-sm text-gray-500 dark:text-gray-400">
                          {activity.activityType} • {activity.state}
                        </p>
                        {activity.assignee && (
                          <p className="text-sm text-gray-500 dark:text-gray-400">
                            Assigned to: {activity.assignee}
                          </p>
                        )}
                      </div>
                      <div className="text-right">
                        <p className="text-sm text-gray-500 dark:text-gray-400">
                          {activity.startTime
                            ? new Date(activity.startTime).toLocaleString()
                            : 'N/A'}
                        </p>
                        {activity.endTime && (
                          <p className="text-sm text-gray-500 dark:text-gray-400">
                            Completed: {new Date(activity.endTime).toLocaleString()}
                          </p>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};




