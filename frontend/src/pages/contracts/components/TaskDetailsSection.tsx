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
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
        <p className="text-sm text-gray-500 dark:text-gray-400 text-center">
          No active workflow tasks found for this contract
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 space-y-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white">Task Details</h3>
        
        {processInstance && (
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-gray-500 dark:text-gray-400">Process Definition</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {processInstance.bpmnProcessId}
              </p>
            </div>
            <div>
              <p className="text-gray-500 dark:text-gray-400">Status</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {processInstance.state}
              </p>
            </div>
            <div>
              <p className="text-gray-500 dark:text-gray-400">Started</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {new Date(processInstance.startTime).toLocaleString()}
              </p>
            </div>
            {processInstance.endTime && (
              <div>
                <p className="text-gray-500 dark:text-gray-400">Ended</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {new Date(processInstance.endTime).toLocaleString()}
                </p>
              </div>
            )}
          </div>
        )}

        {activeTask && (
          <>
            <div className="grid grid-cols-2 gap-4 text-sm border-t border-gray-200 dark:border-gray-700 pt-4">
              <div>
                <p className="text-gray-500 dark:text-gray-400">Task Name</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {activeTask.taskId || 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-gray-500 dark:text-gray-400">Task Type</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {activeTask.taskType || 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-gray-500 dark:text-gray-400">Assigned To</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {activeTask.assignee || activeTask.candidateUser || 'Unassigned'}
                </p>
              </div>
              <div>
                <p className="text-gray-500 dark:text-gray-400">Status</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {activeTask.state || 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-gray-500 dark:text-gray-400">Created</p>
                <p className="font-medium text-gray-900 dark:text-white">
                  {new Date(activeTask.creationTime).toLocaleString()}
                </p>
              </div>
              {activeTask.dueDate && (
                <div>
                  <p className="text-gray-500 dark:text-gray-400">Due Date</p>
                  <p className="font-medium text-gray-900 dark:text-white">
                    {new Date(activeTask.dueDate).toLocaleString()}
                  </p>
                </div>
              )}
            </div>

            {/* Task Variables */}
            {activeTask.variables && Object.keys(activeTask.variables).length > 0 && (
              <div className="border-t border-gray-200 dark:border-gray-700 pt-4">
                <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Task Variables
                </p>
                <div className="bg-gray-50 dark:bg-gray-700 rounded p-3">
                  <pre className="text-xs text-gray-600 dark:text-gray-300 overflow-x-auto">
                    {JSON.stringify(activeTask.variables, null, 2)}
                  </pre>
                </div>
              </div>
            )}

            {/* Task Actions */}
            <div className="border-t border-gray-200 dark:border-gray-700 pt-4">
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
    </div>
  );
};

