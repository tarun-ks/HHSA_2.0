import { useQuery } from '@tanstack/react-query';
import { workflowService, type TaskHistoryDTO } from '../../../services/workflowService';
import { Loader } from '../../../components/atoms/Loader';

interface TaskHistoryTimelineProps {
  processInstanceKey: number;
}

/**
 * Task History Timeline Component
 * Displays read-only timeline of task creation, assignment, and completion/approval
 * Framework-based: works for any workflow automatically
 */
export const TaskHistoryTimeline = ({ processInstanceKey }: TaskHistoryTimelineProps) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['task-history', processInstanceKey],
    queryFn: () => workflowService.getTaskHistory(processInstanceKey),
    enabled: !!processInstanceKey,
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center p-4">
        <Loader size="sm" />
        <span className="ml-2 text-sm text-gray-500 dark:text-gray-400">Loading task history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 text-sm text-red-600 dark:text-red-400">
        Failed to load task history
      </div>
    );
  }

  const taskHistory = data?.data || [];

  if (taskHistory.length === 0) {
    return (
      <div className="p-4 text-sm text-gray-500 dark:text-gray-400 text-center">
        No task history available yet. Tasks will appear here as the workflow progresses.
      </div>
    );
  }

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return 'N/A';
    try {
      return new Date(dateTime).toLocaleString();
    } catch {
      return dateTime;
    }
  };

  const getStateBadgeColor = (state: string) => {
    const colors: Record<string, string> = {
      CREATED: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300',
      ASSIGNED: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-300',
      CLAIMED: 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-300',
      COMPLETED: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300',
      CANCELLED: 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-300',
    };
    return colors[state] || 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
  };

  return (
    <div className="space-y-4">
      <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-3">
        Task History Timeline
      </h4>
      <div className="relative">
        {/* Timeline line */}
        <div className="absolute left-4 top-0 bottom-0 w-0.5 bg-gray-200 dark:bg-gray-700"></div>
        
        <div className="space-y-6">
          {taskHistory.map((task, index) => (
            <div key={task.taskKey || index} className="relative pl-12">
              {/* Timeline dot */}
              <div className="absolute left-0 top-1.5 w-8 h-8 rounded-full bg-white dark:bg-gray-800 border-2 border-gray-300 dark:border-gray-600 flex items-center justify-center">
                <div className={`w-3 h-3 rounded-full ${
                  task.state === 'COMPLETED' ? 'bg-green-500' :
                  task.state === 'CANCELLED' ? 'bg-red-500' :
                  task.state === 'CLAIMED' ? 'bg-purple-500' :
                  task.state === 'ASSIGNED' ? 'bg-yellow-500' :
                  'bg-blue-500'
                }`}></div>
              </div>

              {/* Task card */}
              <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4 shadow-sm">
                <div className="flex items-start justify-between mb-2">
                  <div className="flex-1">
                    <h5 className="text-sm font-semibold text-gray-900 dark:text-white">
                      {task.taskName || task.taskType || 'Task'}
                    </h5>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      Task ID: {task.taskId || task.taskKey}
                    </p>
                  </div>
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStateBadgeColor(task.state)}`}>
                    {task.state}
                  </span>
                </div>

                {/* Timeline events */}
                <div className="space-y-2 mt-3 text-xs">
                  {task.creationTime && (
                    <div className="flex items-center text-gray-600 dark:text-gray-400">
                      <span className="font-medium w-24">Created:</span>
                      <span>{formatDateTime(task.creationTime)}</span>
                      {task.createdBy && (
                        <span className="ml-2 text-gray-500">by {task.createdBy}</span>
                      )}
                    </div>
                  )}

                  {task.assignmentTime && (
                    <div className="flex items-center text-yellow-600 dark:text-yellow-400">
                      <span className="font-medium w-24">Assigned:</span>
                      <span>{formatDateTime(task.assignmentTime)}</span>
                      {task.assignedTo && (
                        <span className="ml-2">to {task.assignedTo}</span>
                      )}
                    </div>
                  )}

                  {task.claimedTime && (
                    <div className="flex items-center text-purple-600 dark:text-purple-400">
                      <span className="font-medium w-24">Claimed:</span>
                      <span>{formatDateTime(task.claimedTime)}</span>
                      {task.claimedBy && (
                        <span className="ml-2">by {task.claimedBy}</span>
                      )}
                    </div>
                  )}

                  {task.completionTime && (
                    <div className="flex items-center text-green-600 dark:text-green-400">
                      <span className="font-medium w-24">Completed:</span>
                      <span>{formatDateTime(task.completionTime)}</span>
                      {task.completedBy && (
                        <span className="ml-2">by {task.completedBy}</span>
                      )}
                      {task.outcome && (
                        <span className={`ml-2 px-2 py-0.5 rounded text-xs font-semibold ${
                          task.outcome === 'APPROVED' ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300' :
                          task.outcome === 'REJECTED' ? 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-300' :
                          'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
                        }`}>
                          {task.outcome}
                        </span>
                      )}
                    </div>
                  )}

                  {task.completionComment && (
                    <div className="mt-2 p-2 bg-gray-50 dark:bg-gray-700 rounded text-gray-700 dark:text-gray-300">
                      <span className="font-medium">Comment: </span>
                      {task.completionComment}
                    </div>
                  )}

                  {task.dueDate && (
                    <div className="flex items-center text-gray-500 dark:text-gray-400">
                      <span className="font-medium w-24">Due Date:</span>
                      <span>{formatDateTime(task.dueDate)}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

