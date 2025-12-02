import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../../components/atoms/Button';
import { workflowService, WorkflowTask } from '../../../services/workflowService';
import { useToast } from '../../../hooks/useToast';
import { Loader } from '../../../components/atoms/Loader';
import { WorkflowActionButtons } from '../../../frameworks/workflow/components/WorkflowActionButtons';
import { useAppSelector } from '../../../store/hooks';

interface TaskDetailsSectionProps {
  contractId: number;
  processInstanceKey?: number;
  userId?: string;
}

/**
 * Task Details Section Component
 * Displays workflow task information and allows task actions
 */
export const TaskDetailsSection = ({ contractId, processInstanceKey, userId }: TaskDetailsSectionProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();
  const user = useAppSelector((state) => state.auth.user);
  // const [selectedTask, setSelectedTask] = useState<WorkflowTask | null>(null);

  // Get process instance if processInstanceKey is provided
  const { data: processInstanceData } = useQuery({
    queryKey: ['process-instance', processInstanceKey],
    queryFn: () => workflowService.getProcessInstance(processInstanceKey!),
    enabled: !!processInstanceKey,
  });

  // Get tasks for process instance
  const { data: tasksData, isLoading: isLoadingTasks } = useQuery({
    queryKey: ['tasks', processInstanceKey],
    queryFn: () => workflowService.getTasksByProcessInstance(processInstanceKey!),
    enabled: !!processInstanceKey,
  });

  // Get tasks for user (alternative if processInstanceKey not available)
  const { data: userTasksData } = useQuery({
    queryKey: ['user-tasks', userId],
    queryFn: () => workflowService.getTasksForUser(userId!),
    enabled: !!userId && !processInstanceKey,
  });

  const completeTaskMutation = useMutation({
    mutationFn: ({ taskId, variables }: { taskId: number; variables?: Record<string, any> }) =>
      workflowService.completeTask(taskId, variables),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Task completed successfully!');
        queryClient.invalidateQueries({ queryKey: ['tasks', processInstanceKey] });
        queryClient.invalidateQueries({ queryKey: ['user-tasks', userId] });
      } else {
        toast.error(response.error?.description || 'Failed to complete task');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const processInstance = processInstanceData?.data;
  const tasks = tasksData?.data || userTasksData?.data || [];
  const contractTask = tasks.find((task: WorkflowTask) => {
    // Check if task is related to this contract
    return task.variables?.contractId === contractId || 
           task.variables?.contractId === String(contractId) ||
           task.processInstanceKey === processInstanceKey;
  });

  const activeTask = contractTask || tasks[0];

  if (isLoadingTasks) {
    return (
      <div className="flex justify-center py-8">
        <Loader size="md" />
      </div>
    );
  }

  if (!activeTask && !processInstance) {
    return (
      <div className="text-center py-8">
        <p className="text-sm text-gray-500 dark:text-gray-400">
          No active workflow tasks found for this contract
        </p>
      </div>
    );
  }

  const getStatusBadgeColor = (state: string) => {
    const colors: Record<string, string> = {
      CREATED: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300',
      ASSIGNED: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-300',
      CLAIMED: 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-300',
      COMPLETED: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300',
      CANCELLED: 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-300',
      ACTIVE: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300',
    };
    return colors[state] || 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
  };

  return (
    <div className="space-y-6">
      {processInstance && (
        <div>
          <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">
            Process Information
          </h4>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Process Definition</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                {processInstance.bpmnProcessId}
              </p>
            </div>
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Status</p>
              <span className={`badge ${getStatusBadgeColor(processInstance.state)}`}>
                {processInstance.state}
              </span>
            </div>
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Started</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                {processInstance.startTime ? new Date(processInstance.startTime).toLocaleString() : 'N/A'}
              </p>
            </div>
            {processInstance.endTime && (
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Ended</p>
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {new Date(processInstance.endTime).toLocaleString()}
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {activeTask && (
        <>
          <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
            <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">
              Task Information
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Task Name</p>
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {activeTask.taskId || 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Task Type</p>
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {activeTask.taskType || 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Assigned To</p>
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {activeTask.assignee || activeTask.candidateUser || 'Unassigned'}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Status</p>
                <span className={`badge ${getStatusBadgeColor(activeTask.state || '')}`}>
                  {activeTask.state || 'N/A'}
                </span>
              </div>
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Created</p>
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {new Date(activeTask.creationTime).toLocaleString()}
                </p>
              </div>
              {activeTask.dueDate && (
                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Due Date</p>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">
                    {new Date(activeTask.dueDate).toLocaleString()}
                  </p>
                </div>
              )}
            </div>
          </div>

          {/* Task Variables */}
          {activeTask.variables && Object.keys(activeTask.variables).length > 0 && (
            <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
              <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">
                Task Variables
              </h4>
              <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 border border-gray-200 dark:border-gray-600">
                <pre className="text-xs text-gray-600 dark:text-gray-300 overflow-x-auto scrollbar-thin">
                  {JSON.stringify(activeTask.variables, null, 2)}
                </pre>
              </div>
            </div>
          )}

          {/* Task Actions */}
          <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
            <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">
              Actions
            </h4>
            <WorkflowActionButtons
              task={activeTask}
              currentUserId={user?.id || userId || ''}
              onActionComplete={() => {
                queryClient.invalidateQueries({ queryKey: ['tasks', processInstanceKey] });
                queryClient.invalidateQueries({ queryKey: ['user-tasks', userId] });
              }}
              availableActions={['complete', 'assign', 'claim', 'unclaim', 'reassign', 'return']}
              showComplete={true}
              onComplete={() => {
                if (window.confirm('Are you sure you want to complete this task?')) {
                  completeTaskMutation.mutate({
                    taskId: Number(activeTask.taskKey),
                    variables: { approved: true },
                  });
                }
              }}
            />
          </div>
        </>
      )}
    </div>
  );
};

